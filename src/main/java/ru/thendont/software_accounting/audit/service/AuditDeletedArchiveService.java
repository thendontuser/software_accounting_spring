package ru.thendont.software_accounting.audit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.audit.entity.AuditDeletedArchive;
import ru.thendont.software_accounting.audit.repository.AuditDeletedArchiveRepository;

import java.util.List;

@Service
public class AuditDeletedArchiveService {

    @Autowired
    private AuditDeletedArchiveRepository auditDeletedArchiveRepository;

    public List<AuditDeletedArchive> findAll() {
        return (List<AuditDeletedArchive>) auditDeletedArchiveRepository.findAll();
    }
}