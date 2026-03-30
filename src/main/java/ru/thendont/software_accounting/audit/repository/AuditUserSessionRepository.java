package ru.thendont.software_accounting.audit.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.audit.entity.AuditUserSession;

import java.util.Optional;

@Repository
public interface AuditUserSessionRepository extends CrudRepository<AuditUserSession, Long> {

    Optional<AuditUserSession> findBySessionId(String sessionId);
}