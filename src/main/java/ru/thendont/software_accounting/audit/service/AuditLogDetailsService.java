package ru.thendont.software_accounting.audit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.audit.entity.AuditLogDetails;
import ru.thendont.software_accounting.audit.repository.AuditLogDetailsRepository;

import java.util.List;

@Service
public class AuditLogDetailsService {

    @Autowired
    private AuditLogDetailsRepository auditLogDetailsRepository;

    public List<AuditLogDetails> findAll() {
        return (List<AuditLogDetails>) auditLogDetailsRepository.findAll();
    }
}