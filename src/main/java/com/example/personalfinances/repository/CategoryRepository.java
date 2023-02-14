package com.example.personalfinances.repository;

import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}