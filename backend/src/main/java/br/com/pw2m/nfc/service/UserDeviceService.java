package br.com.pw2m.nfc.service;

import br.com.pw2m.nfc.dto.DeviceClaimDtos;
import br.com.pw2m.nfc.entity.Child;
import br.com.pw2m.nfc.entity.NfcDevice;
import br.com.pw2m.nfc.entity.NfcDeviceStatus;
import br.com.pw2m.nfc.entity.UserAccount;
import br.com.pw2m.nfc.exception.BadRequestException;
import br.com.pw2m.nfc.exception.ConflictException;
import br.com.pw2m.nfc.exception.NotFoundException;
import br.com.pw2m.nfc.repository.NfcDeviceRepository;
import br.com.pw2m.nfc.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class UserDeviceService {

    static final String INVALID_CLAIM = "Dispositivo ou código de ativação inválido.";

    private final NfcDeviceRepository devices;
    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final ChildService children;

    public UserDeviceService(
            NfcDeviceRepository devices,
            UserAccountRepository users,
            PasswordEncoder passwordEncoder,
            ChildService children
    ) {
        this.devices = devices;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.children = children;
    }

    @Transactional
    public DeviceClaimDtos.DeviceResponse claim(String email, DeviceClaimDtos.ClaimRequest request) {
        UserAccount user = requireUser(email);
        String deviceCode = normalizeCode(request.deviceCode());

        NfcDevice device = devices.findForUpdateByDeviceCode(deviceCode)
                .orElseThrow(() -> new BadRequestException(INVALID_CLAIM));

        if (device.getStatus() != NfcDeviceStatus.AVAILABLE || device.getOwner() != null) {
            throw new ConflictException("Este dispositivo já foi ativado ou não está disponível.");
        }

        if (device.getActivationCodeHash() == null || device.getActivationCodeHash().isBlank()) {
            throw new BadRequestException(INVALID_CLAIM);
        }

        boolean validPin = passwordEncoder.matches(
                request.activationCode().trim(),
                device.getActivationCodeHash()
        );
        if (!validPin) {
            throw new BadRequestException(INVALID_CLAIM);
        }

        device.setOwner(user);
        device.setChild(null);
        device.applyOperationalStatus(NfcDeviceStatus.CLAIMED);
        device.setClaimedAt(Instant.now());
        devices.save(device);
        return response(device);
    }

    @Transactional(readOnly = true)
    public List<DeviceClaimDtos.DeviceResponse> listMyDevices(String email) {
        UserAccount user = requireUser(email);
        return devices.findAllByOwnerIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::response)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeviceClaimDtos.DeviceResponse getMyDevice(String email, Long deviceId) {
        UserAccount user = requireUser(email);
        NfcDevice device = devices.findByIdAndOwnerId(deviceId, user.getId())
                .orElseThrow(() -> new NotFoundException("Dispositivo não encontrado."));
        return response(device);
    }

    @Transactional
    public DeviceClaimDtos.DeviceResponse assign(
            String email,
            Long deviceId,
            DeviceClaimDtos.AssignRequest request
    ) {
        UserAccount owner = requireUser(email);
        NfcDevice device = devices.findOwnedForUpdate(deviceId, owner.getId())
                .orElseThrow(() -> new NotFoundException("Dispositivo não encontrado."));

        if (device.getStatus() != NfcDeviceStatus.CLAIMED) {
            throw new ConflictException("Este dispositivo não está disponível para vínculo.");
        }
        if (device.getChild() != null) {
            throw new ConflictException("Este dispositivo já está vinculado a uma pessoa.");
        }

        Child person = children.requireOwned(request.personId(), owner);
        device.setDeviceName(request.deviceName().trim());
        device.setChild(person);
        device.applyOperationalStatus(NfcDeviceStatus.ACTIVE);
        devices.save(device);
        return response(device);
    }

    @Transactional
    public DeviceClaimDtos.DeviceResponse unassign(String email, Long deviceId) {
        UserAccount owner = requireUser(email);
        NfcDevice device = devices.findOwnedForUpdate(deviceId, owner.getId())
                .orElseThrow(() -> new NotFoundException("Dispositivo não encontrado."));

        if (device.getStatus() != NfcDeviceStatus.ACTIVE
                && device.getStatus() != NfcDeviceStatus.SUSPENDED) {
            throw new ConflictException("Este dispositivo não está vinculado.");
        }

        device.setChild(null);
        device.applyOperationalStatus(NfcDeviceStatus.CLAIMED);
        devices.save(device);
        return response(device);
    }

    private UserAccount requireUser(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }

    private DeviceClaimDtos.DeviceResponse response(NfcDevice device) {
        Long personId = null;
        String personName = null;
        if (device.getChild() != null) {
            personId = device.getChild().getId();
            personName = device.getChild().getFullName();
        }
        return new DeviceClaimDtos.DeviceResponse(
                device.getId(),
                device.getDeviceToken(),
                device.getDeviceName(),
                device.getCategory(),
                device.getStatus(),
                personId,
                personName,
                device.getClaimedAt(),
                device.getManufacturedAt()
        );
    }
}
