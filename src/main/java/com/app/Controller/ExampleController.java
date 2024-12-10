package com.app.Controller;

import com.app.Model.ContentResponseDto;
import com.app.service.ContentExtractorControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api")
public class ExampleController {

    @GetMapping("/message")
    public String getMessage() {
        return "Hello QWERTY";
    }

    @Autowired
    private ContentExtractorControl contentExtractorControl;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContentResponseDto> classify(@Valid @NotNull @RequestParam("file") final MultipartFile pdfFile) {
        if (pdfFile.isEmpty()) {
            throw new IllegalArgumentException("Uploaded PDF file is empty!");
        }
        return ResponseEntity.ok().body(ContentResponseDto.builder().content(contentExtractorControl.extractContent(pdfFile)).build());
    }
}