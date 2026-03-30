package ru.thendont.software_accounting.audit.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.audit.entity.AuditDeletedArchive;

@Repository
public interface AuditDeletedArchiveRepository extends CrudRepository<AuditDeletedArchive, Long> { }