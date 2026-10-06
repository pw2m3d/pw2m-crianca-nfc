package br.com.pw2m.nfc.dto;

import br.com.pw2m.nfc.entity.NfcDeviceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public final class NfcDtos {

    private NfcDtos() {
    }

    public record CreateDeviceRequest(

            @NotBlank
            @Size(max = 120)
            String deviceName,

            @NotNull
            NfcDeviceCategory category

    ) {
    }


    public record DeviceResponse(

            Long id,

            String deviceName,

            NfcDeviceCategory category,

            String deviceToken,

            boolean active,

            Instant createdAt

    ) {
    }

}