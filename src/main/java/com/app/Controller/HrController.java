package com.app.Controller;

import com.app.dto.ApplicantDTO;
import com.app.dto.HrCandidateDTO;
import com.app.entity.CurrentStatus;
import com.app.service.ApplicantService; // Import the service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hr")
public class HrController {

    @Autowired
    private ApplicantService applicantService;

    @GetMapping("/applicants")
    public List<ApplicantDTO> getApplicants() {
        // Use the service to get the list of applicants
        return applicantService.getAllApplicants();
    }

    @GetMapping("/applicants/sorted")
    public List<ApplicantDTO> getApplicantsSortedByCompatibility() {
        // Use the service to get the list of applicants sorted by compatibility
        return applicantService.getApplicantsSortedByCompatibility();
    }


    @GetMapping("/candidates")
    public List<HrCandidateDTO> getCandidatesByJD(@RequestParam("jdNumber") String jdNumber) {  //http://localhost:8080/api/hr/candidates?jdNumber=123.45
        // Fetch the candidates based on JD_number and optional search term
        return applicantService.getCandidatesByJD(jdNumber);
    }

    @PostMapping("/candidates/results")
    public void saveSelectedCandidates(@RequestBody List<HrCandidateDTO> candidates) {
        applicantService.saveSelectedCandidates(candidates);
    }

    @GetMapping("/candidates/status/count")
    public Map<CurrentStatus, Long> getCandidatesCountByStatus() {
        return applicantService.getCountByCurrentStatus();
    }

    @GetMapping("/applications/count/jd")
    public Map<String, Long> getApplicationsCountByJD() {
        return applicantService.getApplicationsCountByJD();
    }
}