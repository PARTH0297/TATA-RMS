package com.app.service;

import com.app.dto.ApplicantDTO;
import com.app.dto.HrCandidateDTO;
import com.app.entity.CandidateResult;
import com.app.entity.ContentEntity;
import com.app.entity.CurrentStatus;
import com.app.entity.TestStatus;
import com.app.repository.ContentRepository;
import com.app.repository.CandidateResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;


@Service
public class ApplicantService {

    @Autowired
    private ContentRepository contentRepository; // Inject your repository

    @Autowired
    private CandidateResultRepository candidateResultRepository;

    public List<ApplicantDTO> getAllApplicants() {
        // Fetch all content entities from the repository
        List<ContentEntity> contentEntities = contentRepository.findAll();

        // Convert ContentEntity to ApplicantDTO
        return contentEntities.stream()
                .map(content -> ApplicantDTO.builder()
                        .name(content.getName())
                        .__email(content.get__email())
                        .JD_Role(content.getJD_Role())
                        .JD_number(content.getJD_number())
                        .compatibility(content.getCompatibility())
                        .skills(content.getMatchingSkills())
                        .missingSkills(content.getMissingSkills())
                        .currentStatus(content.getCurrentStatus())
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
                        .__email(content.get__email())
                        .JD_Role(content.getJD_Role())
                        .JD_number(content.getJD_number())
                        .compatibility(content.getCompatibility())
                        .skills(content.getMatchingSkills())
                        .missingSkills(content.getMissingSkills())
                        .currentStatus(content.getCurrentStatus())
                        .build())
                .sorted(Comparator.comparing(ApplicantDTO::getCompatibility).reversed()) // Sort in descending order
                .collect(Collectors.toList());
    }


    public List<HrCandidateDTO> getCandidatesByJD(String jdNumber) {
        // Fetch candidates filtered by JD_number
        List<ContentEntity> contentEntities = contentRepository.findAll()
                .stream()
                .filter(content -> content.getJD_number().equals(jdNumber) && content.getCurrentStatus() == CurrentStatus.SCREEN_SELECTED)
                .collect(Collectors.toList());

        // Convert to HrCandidateDTO
        return contentEntities.stream()
                .map(content -> HrCandidateDTO.builder()
                        .name(content.getName())
                        .__email(content.get__email())
                        .skills(content.getMatchingSkills())
                        .missingSkills(content.getMissingSkills())
                        .compatibility(content.getCompatibility())
                        .isSelect(false) // Default to false for checkbox
                        .JD_number(content.getJD_number())
                        .build())
                .collect(Collectors.toList());
    }

    public void saveSelectedCandidates(List<HrCandidateDTO> candidates) {
        for (HrCandidateDTO candidate : candidates) {
            if (candidate.isSelect()) {
                // Update the currentStatus of the corresponding ContentEntity
                ContentEntity contentEntity = contentRepository.findAll()
                        .stream()
                        .filter(content -> content.getName().equals(candidate.getName())
                                && content.getJD_number().equals(candidate.getJD_number())
                                && content.get__email().equals(candidate.get__email()) )

                        .findFirst()
                        .orElse(null);

                if (contentEntity != null) {
                    contentEntity.setCurrentStatus(CurrentStatus.MCQ_SCHEDULED); // Update status to QUIZ
                    contentRepository.save(contentEntity); // Save the updated entity
                }

                // Create and save the CandidateResult entity
                CandidateResult result = CandidateResult.builder()
                        .candidateName(candidate.getName())
                        .testKey(candidate.getJD_number())
                        .score(0) // Set score to null for now it will be updated later in Quiz service
                        .testStatus(TestStatus.PENDING) // Set status to PENDING
                        .build();
                candidateResultRepository.save(result); // Save the CandidateResult entity
            }
        }
    }


    public Map<CurrentStatus, Long> getCountByCurrentStatus() {
        // Fetch all content entities from the repository
        List<ContentEntity> contentEntities = contentRepository.findAll();

        // Count the number of candidates by CurrentStatus
        return contentEntities.stream()
                .collect(Collectors.groupingBy(ContentEntity::getCurrentStatus, Collectors.counting()));
    }

    public Map<String, Long> getApplicationsCountByJD() {
        // Fetch all content entities from the repository
        List<ContentEntity> contentEntities = contentRepository.findAll();

        // Count the number of applications by JD_number
        return contentEntities.stream()
                .collect(Collectors.groupingBy(ContentEntity::getJD_number, Collectors.counting()));
    }
}