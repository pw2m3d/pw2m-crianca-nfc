package br.com.pw2m.nfc.service;

import br.com.pw2m.nfc.dto.ChildDtos;
import br.com.pw2m.nfc.entity.Child;
import br.com.pw2m.nfc.entity.EmergencyContact;
import br.com.pw2m.nfc.entity.MedicalInfo;
import br.com.pw2m.nfc.entity.UserAccount;
import br.com.pw2m.nfc.exception.NotFoundException;
import br.com.pw2m.nfc.repository.ChildRepository;
import br.com.pw2m.nfc.repository.EmergencyContactRepository;
import br.com.pw2m.nfc.repository.MedicalInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ChildService {

    private final ChildRepository children;
    private final EmergencyContactRepository contacts;
    private final MedicalInfoRepository medical;

    public ChildService(
            ChildRepository children,
            EmergencyContactRepository contacts,
            MedicalInfoRepository medical
    ) {
        this.children = children;
        this.contacts = contacts;
        this.medical = medical;
    }

    @Transactional(readOnly = true)
    public List<ChildDtos.ChildSummaryResponse> list(UserAccount owner) {
        return children.findAllByOwnerIdOrderByCreatedAtDesc(owner.getId())
                .stream()
                .map(this::summary)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChildDtos.ChildResponse get(Long childId, UserAccount owner) {
        return response(requireOwned(childId, owner));
    }

    @Transactional
    public ChildDtos.ChildResponse create(
            ChildDtos.ChildRequest request,
            UserAccount owner
    ) {
        Child child = new Child();
        child.setOwner(owner);
        apply(child, request);
        children.save(child);

        saveMedical(child, request.medical());
        replaceContacts(child, request.contacts());

        return response(child);
    }

    @Transactional
    public ChildDtos.ChildResponse update(
            Long childId,
            ChildDtos.ChildRequest request,
            UserAccount owner
    ) {
        Child child = requireOwned(childId, owner);
        apply(child, request);
        children.save(child);

        saveMedical(child, request.medical());
        replaceContacts(child, request.contacts());

        return response(child);
    }

    @Transactional
    public void delete(Long childId, UserAccount owner) {
        Child child = requireOwned(childId, owner);
        children.delete(child);
    }

    @Transactional(readOnly = true)
    public Child requireOwned(Long childId, UserAccount owner) {
        return children.findByIdAndOwnerId(childId, owner.getId())
                .orElseThrow(() -> new NotFoundException("Criança não encontrada."));
    }

    private void apply(Child child, ChildDtos.ChildRequest request) {
        child.setFullName(request.fullName().trim());
        child.setBirthDate(request.birthDate());
        child.setBloodType(blankToNull(request.bloodType()));
        child.setPhotoUrl(blankToNull(request.photoUrl()));
        child.setAddressLine(blankToNull(request.addressLine()));
        child.setCity(blankToNull(request.city()));
        child.setState(request.state() == null ? null : request.state().trim().toUpperCase());
        child.setPublicEnabled(request.publicEnabled() == null || request.publicEnabled());
    }

    private void saveMedical(Child child, ChildDtos.MedicalInfoRequest request) {
        if (request == null) {
            return;
        }

        MedicalInfo info = medical.findByChildId(child.getId())
                .orElseGet(() -> {
                    MedicalInfo m = new MedicalInfo();
                    m.setChild(child);
                    return m;
                });

        info.setAllergies(blankToNull(request.allergies()));
        info.setConditions(blankToNull(request.conditions()));
        info.setMedications(blankToNull(request.medications()));
        info.setHealthPlan(blankToNull(request.healthPlan()));
        info.setHealthPlanNumber(blankToNull(request.healthPlanNumber()));
        info.setEmergencyNotes(blankToNull(request.emergencyNotes()));

        medical.save(info);
    }

    private void replaceContacts(
            Child child,
            List<ChildDtos.EmergencyContactRequest> requests
    ) {
        contacts.deleteAllByChildId(child.getId());

        if (requests == null) {
            return;
        }

        int fallbackPriority = 1;

        for (ChildDtos.EmergencyContactRequest request : requests) {
            EmergencyContact contact = new EmergencyContact();
            contact.setChild(child);
            contact.setName(request.name().trim());
            contact.setRelationLabel(request.relation().trim());
            contact.setPhone(request.phone().trim());
            contact.setWhatsapp(blankToNull(request.whatsapp()));
            contact.setPriority(
                    request.priority() == null ? fallbackPriority : request.priority()
            );
            contacts.save(contact);
            fallbackPriority++;
        }
    }

    private ChildDtos.ChildSummaryResponse summary(Child child) {
        return new ChildDtos.ChildSummaryResponse(
                child.getId(),
                child.getPublicToken(),
                child.getFullName(),
                age(child.getBirthDate()),
                child.getBloodType(),
                child.isPublicEnabled()
        );
    }

    private ChildDtos.ChildResponse response(Child child) {
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

        return new ChildDtos.ChildResponse(
                child.getId(),
                child.getPublicToken(),
                child.getFullName(),
                child.getBirthDate(),
                age(child.getBirthDate()),
                child.getBloodType(),
                child.getPhotoUrl(),
                child.getAddressLine(),
                child.getCity(),
                child.getState(),
                child.isPublicEnabled(),
                info == null ? null : new ChildDtos.MedicalInfoResponse(
                        info.getAllergies(),
                        info.getConditions(),
                        info.getMedications(),
                        info.getHealthPlan(),
                        info.getHealthPlanNumber(),
                        info.getEmergencyNotes()
                ),
                contactResponses
        );
    }

    private Integer age(LocalDate birthDate) {
        return birthDate == null ? null : Period.between(birthDate, LocalDate.now()).getYears();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
