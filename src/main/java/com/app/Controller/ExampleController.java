package com.app.Controller;


import com.app.service.ContentExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api")
public class ExampleController {



    @Autowired
    private ContentExtractorService contentExtractorService;

    @GetMapping("/message")
    public String getMessage() {
        return "Hello QWERTY";
    }


    @PostMapping(value = "/classify/{JD_number}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> classify(@PathVariable("JD_number") String JD_number, @Valid @NotNull @RequestParam("file") final MultipartFile pdfFile) {

        if (pdfFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Uploaded PDF file is empty!");
        }

        try {
            String email = contentExtractorService.extractContent(pdfFile, JD_number);
            return ResponseEntity.ok(email);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

}