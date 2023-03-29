package com.example.personalfinances.repository;

import com.example.personalfinances.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, java.lang.String> {
    public List<Keyword> findByIsAssigned(boolean isAssigned);
}
