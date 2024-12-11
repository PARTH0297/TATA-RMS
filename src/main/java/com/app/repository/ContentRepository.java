package com.app.repository;

import com.app.entity.ContentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentRepository extends MongoRepository<ContentEntity, String> {
}