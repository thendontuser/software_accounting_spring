package ru.thendont.software_accounting.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.License;

import java.math.BigDecimal;

@Repository
public interface LicenseRepository extends CrudRepository<License, Long> {

    @Query(value = "SELECT public.calculate_total_cost()", nativeQuery = true)
    BigDecimal getTotalCost();
}