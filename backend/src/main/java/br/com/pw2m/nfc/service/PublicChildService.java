package br.com.pw2m.nfc.service;

import br.com.pw2m.nfc.dto.ChildDtos;
import br.com.pw2m.nfc.entity.AccessLog;
import br.com.pw2m.nfc.entity.Child;
import br.com.pw2m.nfc.entity.MedicalInfo;
import br.com.pw2m.nfc.entity.NfcDevice;
import br.com.pw2m.nfc.exception.NotFoundException;
import br.com.pw2m.nfc.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class PublicChildService {

    private final ChildRepository children;
    private final EmergencyContactRepository contacts;
    private final MedicalInfoRepository medical;
    private final NfcDeviceRepository devices;
    private final AccessLogRepository logs;

    public PublicChildService(
            ChildRepository children,
            EmergencyContactRepository contacts,
            MedicalInfoRepository medical,
            NfcDeviceRepository devices,
            AccessLogRepository logs
    ) {
        this.children = children;
        this.contacts = contacts;
        this.medical = medical;
        this.devices = devices;
        this.logs = logs;
    }

    @Transactional
    public ChildDtos.PublicChildResponse get(String publicToken) {
        Child child = requirePublic(publicToken);
        log(child, null);
        return response(child);
    }

    @Transactional
    public ChildDtos.PublicChildResponse getByDevice(
            String publicToken,
            String deviceToken
    ) {
        Child child = requirePublic(publicToken);

        NfcDevice device = devices
                .findByDeviceTokenAndChildPublicTokenAndActiveTrue(deviceToken, publicToken)
                .orElseThrow(() -> new NotFoundException("Tag NFC desativada ou não encontrada."));

        log(child, device);
        return response(child);
    }

    private Child requirePublic(String token) {
        return children.findByPublicTokenAndPublicEnabledTrue(token)
                .orElseThrow(() -> new NotFoundException("Ficha pública não encontrada."));
    }

    private void log(Child child, NfcDevice device) {
        AccessLog log = new AccessLog();
        log.setChild(child);
        log.setDevice(device);
        logs.save(log);
    }

    private ChildDtos.PublicChildResponse response(Child child) {
        MedicalInfo info = medical.findByChildId(child.getId()).orElse(null);

        List<ChildDtos.EmergencyContactResponse> contactResponses =
                contacts.findAllByChildIdOrderByPriorityAscIdAsc(child.getId())
                        .stream()
                        .map(c -> new ChildDtos.EmergencyContactResponse(
                                c.getId(),
                                c.getName(),
                                c.getRelationLabel(),
                                c.getPhone(),
                                c.getWhatsapp(),
                                c.getPriority()
                        ))
                        .toList();

        ChildDtos.MedicalInfoResponse medicalResponse =
                info == null ? null : new ChildDtos.MedicalInfoResponse(
                        info.getAllergies(),
                        info.getConditions(),
                        info.getMedications(),
                        info.getHealthPlan(),
                        info.getHealthPlanNumber(),
                        info.getEmergencyNotes()
                );

        return new ChildDtos.PublicChildResponse(
                child.getFullName(),
                age(child.getBirthDate()),
                child.getBloodType(),
                child.getPhotoUrl(),
                medicalResponse,
                contactResponses
        );
    }

    private Integer age(LocalDate birthDate) {
        return birthDate == null ? null : Period.between(birthDate, LocalDate.now()).getYears();
    }
}
