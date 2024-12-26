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

    private String __email; // New field for email

    private String JD_number; // New field for JD_number

    private String JD_Role;

    private String content;

    private List<String> Skills;

    private Double compatibility;

    private String file_name;

    private CurrentStatus currentStatus; // Use enum for current status


    // getters and setters
}