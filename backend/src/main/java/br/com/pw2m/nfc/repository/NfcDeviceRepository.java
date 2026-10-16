package br.com.pw2m.nfc.repository;

import br.com.pw2m.nfc.entity.NfcDevice;
import br.com.pw2m.nfc.entity.NfcDeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface NfcDeviceRepository
        extends JpaRepository<NfcDevice, Long> {


    /*
     * =========================================================
     * CONSULTAS PELO CÓDIGO DO DISPOSITIVO
     * =========================================================
     */

    boolean existsByDeviceToken(
            String deviceToken
    );


    Optional<NfcDevice> findByDeviceToken(
            String deviceToken
    );

    Optional<NfcDevice> findByDeviceTokenIgnoreCase(
            String deviceToken
    );


    Optional<NfcDevice> findByDeviceTokenAndStatus(
            String deviceToken,
            NfcDeviceStatus status
    );


    /*
     * =========================================================
     * CONSULTAS DO MODELO ANTIGO
     *
     * Ainda são utilizadas pela tela:
     *
     * /criancas/{id}/nfcs
     *
     * Não remover agora.
     * =========================================================
     */

    List<NfcDevice> findAllByChildIdOrderByCreatedAtDesc(
            Long childId
    );


    Optional<NfcDevice> findByIdAndChildId(
            Long id,
            Long childId
    );


    /*
     * =========================================================
     * NOVO MODELO
     *
     * NFCs pertencentes ao cliente.
     * =========================================================
     */

    List<NfcDevice> findAllByOwnerIdOrderByCreatedAtDesc(
            Long ownerId
    );


    Optional<NfcDevice> findByIdAndOwnerId(
            Long id,
            Long ownerId
    );


    /*
     * =========================================================
     * ESTOQUE / ADMINISTRAÇÃO
     * =========================================================
     */

    List<NfcDevice> findAllByStatusOrderByCreatedAtDesc(
            NfcDeviceStatus status
    );
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       select d
       from NfcDevice d
       where upper(d.deviceToken) = upper(:deviceCode)
       """)
    Optional<NfcDevice> findForUpdateByDeviceCode(
            @Param("deviceCode")
            String deviceCode
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       select d
       from NfcDevice d
       where d.id = :deviceId
         and d.owner.id = :ownerId
       """)
    Optional<NfcDevice> findOwnedForUpdate(
            @Param("deviceId") Long deviceId,
            @Param("ownerId") Long ownerId
    );
}