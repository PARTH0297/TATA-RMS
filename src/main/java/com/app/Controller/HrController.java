package com.app.Controller;

import com.app.dto.ApplicantDTO;
import com.app.service.ApplicantService; // Import the service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hr")
public class HrController {

    @Autowired
    private ApplicantService applicantService; // Inject the service

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
}