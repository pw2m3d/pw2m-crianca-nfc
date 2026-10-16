package br.com.pw2m.nfc.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class DeviceCodeGenerator {

    /*
     * Evitamos caracteres fáceis de confundir:
     *
     * 0 / O
     * 1 / I
     *
     * Isso ajuda quando o cliente precisar
     * digitar o código manualmente.
     */
    private static final String ALPHABET =
            "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    private final SecureRandom random =
            new SecureRandom();


    public String generateDeviceCode() {

        return "PW2M-"
                + block()
                + "-"
                + block()
                + "-"
                + block();
    }


    public String generateActivationPin() {

        int number =
                100000
                        + random.nextInt(900000);

        return String.valueOf(number);
    }


    private String block() {

        StringBuilder result =
                new StringBuilder(4);

        for (int i = 0; i < 4; i++) {

            int index =
                    random.nextInt(
                            ALPHABET.length()
                    );

            result.append(
                    ALPHABET.charAt(index)
            );
        }

        return result.toString();
    }
}