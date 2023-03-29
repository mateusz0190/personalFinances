package com.example.personalfinances.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "keyword")
public class Keyword {
    @Id
    private String keywordName;
    private boolean isAssigned;

}
