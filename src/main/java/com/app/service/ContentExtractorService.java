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
    private static final String API_URL = "https://flaskpop.onrender.com/process";

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
            String jobDescription =  "We are seeking a talented and motivated Full Stack Developer to join our dynamic team. The ideal candidate will have a strong background in both front-end and back-end development, with a passion for building scalable web applications and a keen eye for detail. You will work closely with our product and design teams to create user-friendly and efficient applications that meet our business needs.Key Responsibilities:- Design and Development: Develop and maintain web applications using modern frameworks and technologies. Collaborate with UI/UX designers to implement responsive and visually appealing user interfaces. Write clean, maintainable, and efficient code for both front-end and back-end components.- Database Management: Design and manage databases, ensuring data integrity and security. Optimize database queries for performance and scalability.- API Development: Create and maintain RESTful APIs to support front-end functionality. Integrate third-party APIs and services as needed.- Testing and Debugging: Conduct thorough testing and debugging of applications to ensure high-quality deliverables. Implement automated testing frameworks to improve code quality and reliability.- Collaboration: Work closely with cross-functional teams, including product managers, designers, and other developers, to define project requirements and deliver solutions. Participate in code reviews and provide constructive feedback to peers.- Continuous Learning: Stay up-to-date with emerging technologies and industry trends to continuously improve skills and knowledge. Contribute to team knowledge sharing and best practices.Required Skills and Qualifications:- Education: Bachelor’s degree in Computer Science, Information Technology, or a related field (or equivalent experience).- Technical Skills: Proficiency in front-end technologies such as HTML, CSS, and JavaScript frameworks (e.g., React, Angular, or Vue.js). Strong experience with back-end technologies such as Node.js, Express, or similar frameworks. Familiarity with database management systems (e.g., MySQL, PostgreSQL, MongoDB). Experience with version control systems (e.g., Git).- Soft Skills: Excellent problem-solving skills and attention to detail. Strong communication and collaboration skills. Ability to work independently and manage multiple tasks effectively.- Experience: [X] years of experience in full stack development or related roles. Proven track record of delivering high-quality web applications.Preferred Qualifications:- Experience with cloud services (e.g., AWS, Azure, Google Cloud). Familiarity with DevOps practices and CI/CD pipelines. Knowledge of Agile methodologies and project management tools (e.g., Jira, Trello).Benefits:- Competitive salary and performance-based bonuses. Flexible working hours and remote work options. Opportunities for professional development and career growth. [List any additional benefits, such as health insurance, retirement plans, etc.]\n\nHow to Apply:\nIf you are passionate about full stack development and want to be part of a collaborative and innovative team, we would love to hear from you! Please submit your resume and a cover letter detailing your relevant experience to [Insert Application Email/Link].";
            Map<String, String> json = new HashMap<>();
            json.put("resume", text);
            json.put("job_description", jobDescription);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(json, headers);

            // Call the similarity API
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, new ParameterizedTypeReference<Map<String, Object>>() {});
            // List<Object> matchPercentage = (List<Object>) response.getBody().get("match_percentage");

            // Parse response data
            Map<String, Object> responseBody = response.getBody();
            Object compatibilityObj = responseBody.get("compatibility");

            // Check the type of compatibility and cast accordingly
            Double compatibility;
            if (compatibilityObj instanceof Integer) {
                compatibility = ((Integer) compatibilityObj).doubleValue(); // Convert Integer to Double
            } else if (compatibilityObj instanceof Double) {
                compatibility = (Double) compatibilityObj; // Directly cast to Double
            } else {
                compatibility = 0.0; // Default value if type is unexpected
            }

            List<String> matchingSkills = (List<String>) responseBody.get("matchingSkills");
            List<String> missingSkills = (List<String>) responseBody.get("missingSkills");
            String name = (String) responseBody.get("name");
            String email = (String) responseBody.get("email");

            // Create ContentEntity object
            ContentEntity contentEntity = ContentEntity.builder()
                    .name(name) // Set name from API response
                    .__email(email) // Set email from API response
                    .JD_number(JD_number) // Set JD_number from the URL parameter
                    .JD_Role("full_stack") // Hardcoded JD_Role
                    .content(text) // Set the extracted content
                    .matchingSkills(matchingSkills) // Set matching skills from response
                    .missingSkills(missingSkills)
                    .compatibility(compatibility) // Set compatibility from response
                    .currentStatus(CurrentStatus.SCREEN_SELECTED) // Set default current status
                    .file_name(multipartFile.getOriginalFilename()) // Set the file name
                    .build();

            // Save the content entity to MongoDB
            contentRepository.save(contentEntity);

            return  contentEntity.getCompatibility();

        } catch (final Exception ex) {
            log.error("Error in ContentExtractorService", ex);
            return 0.0;
        }
    }
}