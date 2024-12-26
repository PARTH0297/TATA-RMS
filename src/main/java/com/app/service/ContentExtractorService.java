package com.app.service;

import com.app.entity.ContentEntity;
import com.app.entity.CurrentStatus;
import com.app.repository.ContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ContentExtractorService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String STORAGE_DIR = "C:/uploded"; // Base directory to store PDFs
    private static final String API_URL = "http://127.0.0.1:5000/match";

    public double extractContent(final MultipartFile multipartFile,String JD_number) {
        String text = "";
        File storedFile = null;

        try {
            // Create the base storage directory if it doesn't exist
            Path storagePath = Paths.get(STORAGE_DIR);
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }

            // Create a subdirectory for the JD_number
            String jdDirectory = JD_number;
            Path jdPath = storagePath.resolve(jdDirectory);
            if (!Files.exists(jdPath)) {
                Files.createDirectories(jdPath);
            }

            // Create a file in the JD_number directory
            storedFile = new File(jdPath.toFile(), multipartFile.getOriginalFilename());
            multipartFile.transferTo(storedFile);

            // Extract text from the PDF file
            try (final PDDocument document = PDDocument.load(storedFile)) {
                final PDFTextStripper pdfStripper = new PDFTextStripper();
                text = pdfStripper.getText(document);
            }

            // Print the extracted text to the console
            System.out.println("Extracted Text from PDF:");
            System.out.println(text);

            // Prepare the request for the similarity API
            String jobDescription = "Looking for a software engineer with experience in web development and strong problem-solving skills.";
            Map<String, String> json = new HashMap<>();
            json.put("resume", text);
            json.put("job_description", jobDescription);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(json, headers);

            // Call the similarity API
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, new ParameterizedTypeReference<Map<String, Object>>() {});
            List<Object> matchPercentage = (List<Object>) response.getBody().get("match_percentage");

            // Create ContentEntity object
            ContentEntity contentEntity = ContentEntity.builder()
                    .name(multipartFile.getOriginalFilename().replace(".pdf", "")) // Remove .pdf extension
                    .__email(multipartFile.getOriginalFilename().replace(".pdf", "") + "@gmail.com") // Set email
                    .JD_number(JD_number) // Set JD_number from the URL parameter
                    .JD_Role("full_stack") // Hardcoded JD_Role
                    .content(text) // Set the extracted content
                    .Skills(matchPercentage.subList(1, matchPercentage.size()).stream()
                            .map(Object::toString) // Convert each skill to String
                            .toList()) // Collect to List<String>
                    .compatibility((Double) matchPercentage.get(0)) // First element as compatibility
                    .currentStatus(CurrentStatus.SCREENING) // Set default current status
                    .file_name(multipartFile.getOriginalFilename()) // Set the file name
                    .build();

            // Save the content entity to MongoDB
            contentRepository.save(contentEntity);

            return  contentEntity.getCompatibility();

        } catch (final Exception ex) {
            log.error("Error parsing PDF", ex);
            return 0.0;
        }
    }
}