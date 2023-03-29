package com.example.personalfinances.controller;

import com.example.personalfinances.model.Keyword;
import com.example.personalfinances.service.KeywordService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/keyword")
public class KeywordController {
    private KeywordService keywordService;

    @PostMapping
    public ResponseEntity<HttpStatus> createKeyword(@RequestBody Keyword keyword) {
        keywordService.create(keyword.getKeywordName());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Set<Keyword>> getAll() {
        return ResponseEntity.ok(keywordService.getAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Keyword> getById(@PathVariable("id") java.lang.String id) {
        return ResponseEntity.ok(keywordService.getByKeywordId(id));
    }
    @GetMapping(value = "/free")
    public ResponseEntity<Set<Keyword>> getNotAssigned() {
        return ResponseEntity.ok(keywordService.getByIsAssigned(false));
    }
    @GetMapping(value = "/assigned")
    public ResponseEntity<Set<Keyword>> getAssigned() {
        return ResponseEntity.ok(keywordService.getByIsAssigned(true));
    }

    @PutMapping(value = "releaseAll")
    public ResponseEntity<HttpStatus> releaseAll(){
        Set<Keyword> byIsAssigned = keywordService.getByIsAssigned(true);
        byIsAssigned.stream().forEach(keyword -> {
            keyword.setAssigned(false);
            keywordService.save(keyword);
        });
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @DeleteMapping
    public ResponseEntity<Set<Keyword>> deleteAll() {
        Set<Keyword> all = keywordService.getAll();
        keywordService.deleteAll();
        return ResponseEntity.ok(all);
    }
}
