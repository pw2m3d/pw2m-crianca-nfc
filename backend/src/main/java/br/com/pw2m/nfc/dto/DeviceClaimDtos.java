package br.com.pw2m.nfc.dto;

import br.com.pw2m.nfc.entity.NfcDeviceCategory;
import br.com.pw2m.nfc.entity.NfcDeviceStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class DeviceClaimDtos {

    private DeviceClaimDtos() {
    }


    public record ClaimRequest(

            @NotBlank
            @Size(max = 50)
            String deviceCode,

            @NotBlank
            @Size(min = 6, max = 20)
            String activationCode

    ) {
    }

    public record AssignRequest(

            @NotNull
            Long personId,

            @NotBlank
            @Size(max = 120)
            String deviceName

    ) {
    }
    public record DeviceResponse(

            Long id,

            String deviceCode,

            String deviceName,

            NfcDeviceCategory category,

            NfcDeviceStatus status,

            Long personId,

            String personName,

            Instant claimedAt,

            Instant manufacturedAt

    ) {
    }
}