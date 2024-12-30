package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantDTO {
    private String name;
    private String JD_Role;
    private String JD_number;
    private Double compatibility;
    private List<String> Skills;
}