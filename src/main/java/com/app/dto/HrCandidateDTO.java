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
public class HrCandidateDTO {
    private String name;              // Name of the candidate
    private List<String> skills;      // List of skills
    private Double compatibility;      // Compatibility score
    private boolean isSelect;          // Checkbox for selection
    private String JD_number;          // Job description number (for filtering)
}

