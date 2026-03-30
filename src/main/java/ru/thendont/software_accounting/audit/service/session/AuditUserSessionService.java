package ru.thendont.software_accounting.audit.service.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.audit.entity.AuditUserSession;
import ru.thendont.software_accounting.audit.repository.AuditUserSessionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AuditUserSessionService {

    @Autowired
    private AuditUserSessionRepository auditUserSessionRepository;

    public Optional<AuditUserSession> findById(Long id) {
        return auditUserSessionRepository.findById(id);
    }

    public List<AuditUserSession> findAll() {
        return (List<AuditUserSession>) auditUserSessionRepository.findAll();
    }

    public AuditUserSession save(AuditUserSession auditUserSession) {
        return auditUserSessionRepository.save(auditUserSession);
    }

    public void deleteById(Long id) {
        auditUserSessionRepository.deleteById(id);
    }

    public Optional<AuditUserSession> findBySessionId(String sessionId) {
        return auditUserSessionRepository.findBySessionId(sessionId);
    }
}