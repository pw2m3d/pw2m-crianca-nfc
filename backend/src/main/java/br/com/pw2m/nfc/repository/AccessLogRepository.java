package br.com.pw2m.nfc.repository;

import br.com.pw2m.nfc.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
}
