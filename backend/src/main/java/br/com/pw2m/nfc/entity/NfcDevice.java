package br.com.pw2m.nfc.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "nfc_devices",
        indexes = {

                @Index(
                        name = "idx_nfc_owner",
                        columnList = "owner_id"
                ),

                @Index(
                        name = "idx_nfc_child",
                        columnList = "child_id"
                ),

                @Index(
                        name = "idx_nfc_status",
                        columnList = "status"
                )

        }
)
public class NfcDevice {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;


    /*
     * CLIENTE QUE É DONO DO PRODUTO
     *
     * NULL:
     * dispositivo ainda está no estoque PW2M.
     */

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "owner_id"
    )
    private UserAccount owner;


    /*
     * PESSOA PROTEGIDA
     *
     * Pode ser NULL.
     *
     * Exemplo:
     * cliente acabou de ativar o chaveiro,
     * mas ainda não escolheu quem irá utilizá-lo.
     */

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "child_id"
    )
    private Child child;


    /*
     * Nome dado pelo cliente.
     *
     * Exemplos:
     *
     * Mochila da escola
     * Pulseira azul
     * Chaveiro da vovó
     */

    @Column(
            name = "device_name",
            nullable = false,
            length = 120
    )
    private String deviceName;


    /*
     * Código público PERMANENTE.
     *
     * Será gravado:
     *
     * NFC
     * QR Code
     *
     * Nunca deverá ser alterado depois
     * da fabricação.
     */

    @Column(
            name = "device_token",
            nullable = false,
            unique = true,
            length = 50
    )
    private String deviceToken =
            UUID.randomUUID ().toString ();


    /*
     * PIN secreto de ativação.
     *
     * Armazenaremos apenas BCrypt.
     *
     * Nunca o PIN em texto puro.
     */

    @Column(
            name = "activation_code_hash",
            length = 255
    )
    private String activationCodeHash;


    /*
     * Categoria física.
     */

    @Enumerated(
            EnumType.STRING
    )
    @Column(
            name = "category",
            nullable = false,
            length = 30
    )
    private NfcDeviceCategory category =
            NfcDeviceCategory.OUTRO;


    /*
     * Estado real do dispositivo.
     */

    @Enumerated(
            EnumType.STRING
    )
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private NfcDeviceStatus status =
            NfcDeviceStatus.AVAILABLE;


    /*
     * Mantemos temporariamente para
     * compatibilidade com o código atual.
     *
     * Depois removeremos este boolean
     * e utilizaremos somente "status".
     */

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = false;


    /*
     * Data de fabricação / criação
     * no estoque.
     */

    @Column(
            name = "manufactured_at",
            nullable = false,
            updatable = false
    )
    private Instant manufacturedAt =
            Instant.now ();


    /*
     * Data em que o cliente ativou
     * o dispositivo.
     */

    @Column(
            name = "claimed_at"
    )
    private Instant claimedAt;


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private final Instant createdAt =
            Instant.now ();


    /*
     * GETTERS / SETTERS
     */


    public Long getId () {
        return id;
    }


    public UserAccount getOwner () {
        return owner;
    }

    public void setOwner (
            UserAccount owner
    ) {
        this.owner = owner;
    }


    public Child getChild () {
        return child;
    }

    public void setChild (
            Child child
    ) {
        this.child = child;
    }


    public String getDeviceName () {
        return deviceName;
    }

    public void setDeviceName (
            String deviceName
    ) {
        this.deviceName = deviceName;
    }


    public String getDeviceToken () {
        return deviceToken;
    }

    public void setDeviceToken (
            String deviceToken
    ) {
        this.deviceToken = deviceToken;
    }


    public String getActivationCodeHash () {
        return activationCodeHash;
    }

    public void setActivationCodeHash (
            String activationCodeHash
    ) {
        this.activationCodeHash =
                activationCodeHash;
    }


    public NfcDeviceCategory getCategory () {
        return category;
    }

    public void setCategory (
            NfcDeviceCategory category
    ) {
        this.category = category;
    }


    public NfcDeviceStatus getStatus () {
        return status;
    }

    public void setStatus (
            NfcDeviceStatus status
    ) {
        this.status = status;
    }


    public boolean isActive () {
        return active;
    }

    public void setActive (
            boolean active
    ) {
        this.active = active;
    }


    public Instant getManufacturedAt () {
        return manufacturedAt;
    }

    public void setManufacturedAt (
            Instant manufacturedAt
    ) {
        this.manufacturedAt =
                manufacturedAt;
    }


    public Instant getClaimedAt () {
        return claimedAt;
    }

    public void setClaimedAt (
            Instant claimedAt
    ) {
        this.claimedAt = claimedAt;
    }


    public Instant getCreatedAt () {
        return createdAt;
    }

}