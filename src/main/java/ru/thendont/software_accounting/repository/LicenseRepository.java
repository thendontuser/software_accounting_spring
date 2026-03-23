package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.License;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface LicenseRepository extends CrudRepository<License, Long> {

    @Query(value = "SELECT public.calculate_total_cost()", nativeQuery = true)
    BigDecimal getTotalCost();

    @Query(value = "SELECT * FROM license WHERE id IN (SELECT license_id FROM purchase) AND " +
            "dat_end - current_date <= 30",
            nativeQuery = true)
    Iterable<License> findExpiringLicenses();

    @Query(value = "SELECT (dat_end + make_interval(months => :months))::date FROM license WHERE id = :licenseId",
            nativeQuery = true)
    LocalDate extendEnd(Long licenseId, int months);
}