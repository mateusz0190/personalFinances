package com.example.personalfinances.repository;

import com.example.personalfinances.model.BankAccountNumber;
import com.example.personalfinances.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public interface BankAccountNumberRepository extends JpaRepository<BankAccountNumber, String> {
    List<BankAccountNumber> findByName(String name);
}
