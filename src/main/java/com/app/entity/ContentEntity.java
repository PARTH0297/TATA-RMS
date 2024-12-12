package com.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "contents")
public class ContentEntity {

    @Id
    private ObjectId id = new ObjectId();

    private String name;

    private String file_name;

    private String content;

    private String JD_Role;

    private Double compatibility;

    private List<String> Skills;

    // getters and setters
}