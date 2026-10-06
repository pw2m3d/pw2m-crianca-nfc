package br.com.pw2m.nfc.repository;

import br.com.pw2m.nfc.entity.NfcDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NfcDeviceRepository extends JpaRepository<NfcDevice, Long> {
    List<NfcDevice> findAllByChildIdOrderByCreatedAtDesc(Long childId);
    Optional<NfcDevice> findByIdAndChildId(Long id, Long childId);
    Optional<NfcDevice> findByDeviceTokenAndChildPublicTokenAndActiveTrue(
            String deviceToken,
            String publicToken
    );
}
