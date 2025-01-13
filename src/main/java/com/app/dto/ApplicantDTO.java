package com.app.dto;

import com.app.entity.CurrentStatus;
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
    private String __email;
    private String JD_Role;
    private String JD_number;
    private Double compatibility;
    private List<String> skills;
    private List<String> missingSkills;
    private CurrentStatus currentStatus;
}