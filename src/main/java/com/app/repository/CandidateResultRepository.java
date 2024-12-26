package com.app.repository;

import com.app.entity.CandidateResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateResultRepository extends MongoRepository<CandidateResult, String> {

}