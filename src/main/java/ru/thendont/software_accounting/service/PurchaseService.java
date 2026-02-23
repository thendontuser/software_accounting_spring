package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Purchase;
import ru.thendont.software_accounting.repository.PurchaseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    public Optional<Purchase> findById(Long id) {
        return purchaseRepository.findById(id);
    }

    public List<Purchase> findAll() {
        return (List<Purchase>) purchaseRepository.findAll();
    }

    public Purchase save(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    public void deleteById(Long id) {
        purchaseRepository.deleteById(id);
    }
}