package br.com.pw2m.nfc.repository;

import br.com.pw2m.nfc.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    List<EmergencyContact> findAllByChildIdOrderByPriorityAscIdAsc(Long childId);
    void deleteAllByChildId(Long childId);
}
