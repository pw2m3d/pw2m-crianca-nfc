package br.com.pw2m.nfc.repository;

import br.com.pw2m.nfc.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findAllByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    Optional<Child> findByIdAndOwnerId(Long id, Long ownerId);
    Optional<Child> findByPublicTokenAndPublicEnabledTrue(String publicToken);
}
