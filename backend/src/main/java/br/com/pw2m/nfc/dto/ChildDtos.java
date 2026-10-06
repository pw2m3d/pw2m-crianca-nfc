package br.com.pw2m.nfc.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public final class ChildDtos {

    private ChildDtos() {}

    public record EmergencyContactRequest(
            @NotBlank @Size(max = 160) String name,
            @NotBlank @Size(max = 80) String relation,
            @NotBlank @Size(max = 40) String phone,
            @Size(max = 40) String whatsapp,
            Integer priority
    ) {}

    public record EmergencyContactResponse(
            Long id,
            String name,
            String relation,
            String phone,
            String whatsapp,
            int priority
    ) {}

    public record MedicalInfoRequest(
            String allergies,
            String conditions,
            String medications,
            @Size(max = 160) String healthPlan,
            @Size(max = 120) String healthPlanNumber,
            String emergencyNotes
    ) {}

    public record MedicalInfoResponse(
            String allergies,
            String conditions,
            String medications,
            String healthPlan,
            String healthPlanNumber,
            String emergencyNotes
    ) {}

    public record ChildRequest(
            @NotBlank @Size(max = 160) String fullName,
            LocalDate birthDate,
            @Size(max = 5) String bloodType,
            @Size(max = 500) String photoUrl,
            @Size(max = 255) String addressLine,
            @Size(max = 120) String city,
            @Size(max = 2) String state,
            Boolean publicEnabled,
            @Valid MedicalInfoRequest medical,
            @Valid List<EmergencyContactRequest> contacts
    ) {}

    public record ChildSummaryResponse(
            Long id,
            String publicToken,
            String fullName,
            Integer ageYears,
            String bloodType,
            boolean publicEnabled
    ) {}

    public record ChildResponse(
            Long id,
            String publicToken,
            String fullName,
            LocalDate birthDate,
            Integer ageYears,
            String bloodType,
            String photoUrl,
            String addressLine,
            String city,
            String state,
            boolean publicEnabled,
            MedicalInfoResponse medical,
            List<EmergencyContactResponse> contacts
    ) {}

    public record PublicChildResponse(
            String name,
            Integer ageYears,
            String bloodType,
            String photoUrl,
            MedicalInfoResponse medical,
            List<EmergencyContactResponse> contacts
    ) {}
}
