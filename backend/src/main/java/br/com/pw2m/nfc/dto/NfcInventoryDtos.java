package br.com.pw2m.nfc.dto;

import br.com.pw2m.nfc.entity.NfcDeviceCategory;
import br.com.pw2m.nfc.entity.NfcDeviceStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public final class NfcInventoryDtos {

    private NfcInventoryDtos() {
    }


    /*
     * Solicitação para fabricação de um lote.
     */
    public record ManufactureBatchRequest(

            @Min(1)
            @Max(500)
            int quantity,

            @NotNull
            NfcDeviceCategory category

    ) {
    }


    /*
     * Resposta da fabricação.
     *
     * activationCode aparece SOMENTE
     * neste momento.
     */
    public record ManufacturedDeviceResponse(

            Long id,

            String deviceCode,

            String activationCode,

            NfcDeviceCategory category,

            NfcDeviceStatus status,

            Instant manufacturedAt

    ) {
    }
}