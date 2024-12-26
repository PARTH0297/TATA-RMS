package com.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "CandidateResult")
public class CandidateResult {


    @Id
    private String id;

    private String candidateName;

    private String testKey;

    private int score;

    private TestStatus testStatus;

}
