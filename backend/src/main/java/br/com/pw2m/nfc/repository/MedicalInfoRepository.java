package br.com.pw2m.nfc.repository;

import br.com.pw2m.nfc.entity.MedicalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalInfoRepository extends JpaRepository<MedicalInfo, Long> {
    Optional<MedicalInfo> findByChildId(Long childId);
    void deleteByChildId(Long childId);
}
