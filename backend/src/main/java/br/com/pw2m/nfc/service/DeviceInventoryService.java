package br.com.pw2m.nfc.service;

import br.com.pw2m.nfc.dto.NfcInventoryDtos;
import br.com.pw2m.nfc.entity.NfcDevice;
import br.com.pw2m.nfc.entity.NfcDeviceCategory;
import br.com.pw2m.nfc.entity.NfcDeviceStatus;
import br.com.pw2m.nfc.repository.NfcDeviceRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceInventoryService {

    private final NfcDeviceRepository devices;

    private final DeviceCodeGenerator codeGenerator;

    private final PasswordEncoder passwordEncoder;


    public DeviceInventoryService(
            NfcDeviceRepository devices,
            DeviceCodeGenerator codeGenerator,
            PasswordEncoder passwordEncoder
    ) {

        this.devices =
                devices;

        this.codeGenerator =
                codeGenerator;

        this.passwordEncoder =
                passwordEncoder;
    }


    /*
     * Fabrica um lote de dispositivos.
     */
    @Transactional
    public List<NfcInventoryDtos.ManufacturedDeviceResponse>
    manufactureBatch(
            int quantity,
            NfcDeviceCategory category
    ) {

        List<NfcInventoryDtos.ManufacturedDeviceResponse>
                result =
                new ArrayList<>();


        for (int i = 0; i < quantity; i++) {

            result.add(
                    manufactureOne(category)
            );
        }


        return result;
    }


    /*
     * Fabrica apenas um dispositivo.
     */
    private NfcInventoryDtos.ManufacturedDeviceResponse
    manufactureOne(
            NfcDeviceCategory category
    ) {

        String deviceCode =
                generateUniqueDeviceCode();


        String activationPin =
                codeGenerator
                        .generateActivationPin();


        NfcDevice device =
                new NfcDevice();


        /*
         * Ainda não possui comprador.
         */
        device.setOwner(null);


        /*
         * Ainda não está vinculado
         * a nenhuma pessoa.
         */
        device.setChild(null);


        /*
         * Nome provisório de estoque.
         *
         * Depois o cliente poderá mudar
         * para "Mochila da escola",
         * "Chaveiro do João" etc.
         */
        device.setDeviceName(
                defaultName(category)
        );


        /*
         * Código público permanente.
         */
        device.setDeviceToken(
                deviceCode
        );


        /*
         * Nunca salvamos o PIN puro.
         */
        device.setActivationCodeHash(
                passwordEncoder.encode(
                        activationPin
                )
        );


        device.setCategory(
                category
        );


        device.applyOperationalStatus(
                NfcDeviceStatus.AVAILABLE
        );


        device.setManufacturedAt(
                Instant.now()
        );


        devices.save(device);


        /*
         * O PIN puro é retornado somente aqui,
         * para geração da etiqueta/embalagem.
         */
        return new NfcInventoryDtos
                .ManufacturedDeviceResponse(

                device.getId(),

                device.getDeviceToken(),

                activationPin,

                device.getCategory(),

                device.getStatus(),

                device.getManufacturedAt()
        );
    }


    /*
     * Evita colisão de códigos.
     *
     * A possibilidade já é muito pequena,
     * mas o banco é a validação definitiva.
     */
    private String generateUniqueDeviceCode() {

        String code;


        do {

            code =
                    codeGenerator
                            .generateDeviceCode();

        } while (
                devices.existsByDeviceToken(
                        code
                )
        );


        return code;
    }


    private String defaultName(
            NfcDeviceCategory category
    ) {

        return switch (category) {

            case CHAVEIRO ->
                    "Chaveiro NFC";

            case PULSEIRA ->
                    "Pulseira NFC";

            case MOCHILA ->
                    "NFC para mochila";

            case CARTAO ->
                    "Cartão NFC";

            case ETIQUETA ->
                    "Etiqueta NFC";

            case OUTRO ->
                    "Dispositivo NFC";
        };
    }
}