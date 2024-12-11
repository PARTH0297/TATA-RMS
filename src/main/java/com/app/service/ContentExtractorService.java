package com.app.service;

import com.app.entity.ContentEntity;
import com.app.repository.ContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class ContentExtractorService {

    @Autowired
    private ContentRepository contentRepository;

    private static final String STORAGE_DIR = "C:/uploded"; // Specify the directory to store PDFs

    public String extractContent(final MultipartFile multipartFile) {
        String text = "";
        File storedFile = null;

        try {
            // Create the storage directory if it doesn't exist
            Path storagePath = Paths.get(STORAGE_DIR);
            if (!Files.exists(storagePath)) {
                Files.createDirectories(storagePath);
            }

            // Create a file in the storage directory
            storedFile = new File(storagePath.toFile(), multipartFile.getOriginalFilename());
            multipartFile.transferTo(storedFile);

            // Extract text from the PDF file
            try (final PDDocument document = PDDocument.load(storedFile)) {
                final PDFTextStripper pdfStripper = new PDFTextStripper();
                text = pdfStripper.getText(document);
            }

            // Print the extracted text to the console
            System.out.println("Extracted Text from PDF:");
            System.out.println(text);

            // Save the extracted content to MongoDB
            ContentEntity contentEntity = ContentEntity.builder()
                    .name("sham") // Set the name field to "sham"
                    .file_name(multipartFile.getOriginalFilename())
                    .content(text)
                    .build();
            contentRepository.save(contentEntity);

        } catch (final Exception ex) {
            log.error("Error parsing PDF", ex);
            text = "Error parsing PDF";
        }

        return text;
    }
}