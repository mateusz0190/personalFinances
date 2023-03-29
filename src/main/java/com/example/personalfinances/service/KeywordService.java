package com.example.personalfinances.service;

import com.example.personalfinances.model.Category;
import com.example.personalfinances.model.Keyword;
import com.example.personalfinances.repository.KeywordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class KeywordService {
    private KeywordRepository keywordRepository;


    public Keyword create(String kName) {
        return save(Keyword.builder()
                .keywordName(kName.toLowerCase())
                .isAssigned(false)
                .build());
    }
    public Keyword save(Keyword keyword){
        return keywordRepository.save(keyword);
    }

    public Set<Keyword> getAll() {
        return new HashSet<>(keywordRepository.findAll());
    }

    public Set<Keyword> getByIsAssigned(boolean isAssigned) {
        return new HashSet<>(keywordRepository.findByIsAssigned(isAssigned));
    }

    public Keyword getByKeywordId(String id) {
        id = id.toLowerCase();
        Optional<Keyword> byId = keywordRepository.findById(id);
        String finalId = id;
        Keyword keyword = byId.orElseGet(() -> create(finalId));
        return keyword;
    }
//
//    public Keyword getByIdNotAssigned(String id) {
//        Keyword keywordId = getByKeywordId(id);
//        if (!keywordId.isAssigned()) {
//            return keywordId;
//        }
//        System.out.println("Keyword is used, try select one of free keywords, or release and assign again");
//        return getByIsAssigned(false).iterator().next();

    //}

    public boolean KeywordIsPresentByID(String id) {
        Optional<Keyword> categoryOptional = keywordRepository.findById(id);
        return categoryOptional.isPresent();
    }

    public void deleteAll() {
        keywordRepository.deleteAll();
    }

}
