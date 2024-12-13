package com.app.service;

import com.app.dto.ApplicantDTO;
import com.app.entity.ContentEntity;
import com.app.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicantService {

    @Autowired
    private ContentRepository contentRepository; // Inject your repository

    public List<ApplicantDTO> getAllApplicants() {
        // Fetch all content entities from the repository
        List<ContentEntity> contentEntities = contentRepository.findAll();

        // Convert ContentEntity to ApplicantDTO
        return contentEntities.stream()
                .map(content -> ApplicantDTO.builder()
                        .name(content.getName())
                        .JD_Role(content.getJD_Role())
                        .compatibility(content.getCompatibility())
                        .Skills(content.getSkills())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ApplicantDTO> getApplicantsSortedByCompatibility() {
        // Fetch all content entities from the repository
        List<ContentEntity> contentEntities = contentRepository.findAll();

        // Convert ContentEntity to ApplicantDTO and sort by compatibility
        return contentEntities.stream()
                .map(content -> ApplicantDTO.builder()
                        .name(content.getName())
                        .JD_Role(content.getJD_Role())
                        .compatibility(content.getCompatibility())
                        .Skills(content.getSkills())
                        .build())
                .sorted(Comparator.comparing(ApplicantDTO::getCompatibility).reversed()) // Sort in descending order
                .collect(Collectors.toList());
    }
}