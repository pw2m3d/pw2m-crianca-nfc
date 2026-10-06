package br.com.pw2m.nfc.service;

import br.com.pw2m.nfc.dto.NfcDtos;
import br.com.pw2m.nfc.entity.Child;
import br.com.pw2m.nfc.entity.NfcDevice;
import br.com.pw2m.nfc.entity.UserAccount;
import br.com.pw2m.nfc.exception.NotFoundException;
import br.com.pw2m.nfc.repository.NfcDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NfcDeviceService {

    private final NfcDeviceRepository devices;
    private final ChildService children;

    public NfcDeviceService(
            NfcDeviceRepository devices,
            ChildService children
    ) {
        this.devices = devices;
        this.children = children;
    }

    @Transactional(readOnly = true)
    public List<NfcDtos.DeviceResponse> list(Long childId, UserAccount owner) {
        children.requireOwned(childId, owner);
        return devices.findAllByChildIdOrderByCreatedAtDesc(childId)
                .stream()
                .map(this::response)
                .toList();
    }

    @Transactional
    public NfcDtos.DeviceResponse create(
            Long childId,
            NfcDtos.CreateDeviceRequest request,
            UserAccount owner
    ) {

        Child child =
                children.requireOwned(
                        childId,
                        owner
                );

        NfcDevice device =
                new NfcDevice();

        device.setChild(child);

        device.setDeviceName(
                request.deviceName().trim()
        );

        device.setCategory(
                request.category()
        );

        devices.save(device);

        return response(device);
    }

    @Transactional
    public NfcDtos.DeviceResponse toggle(
            Long childId,
            Long deviceId,
            UserAccount owner
    ) {
        children.requireOwned(childId, owner);

        NfcDevice device = devices.findByIdAndChildId(deviceId, childId)
                .orElseThrow(() -> new NotFoundException("Dispositivo NFC não encontrado."));

        device.setActive(!device.isActive());
        devices.save(device);

        return response(device);
    }

    private NfcDtos.DeviceResponse response(
            NfcDevice device
    ) {

        return new NfcDtos.DeviceResponse(

                device.getId(),

                device.getDeviceName(),

                device.getCategory(),

                device.getDeviceToken(),

                device.isActive(),

                device.getCreatedAt()
        );
    }
}
