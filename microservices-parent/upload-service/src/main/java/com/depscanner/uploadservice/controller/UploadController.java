package com.depscanner.uploadservice.controller;

import com.depscanner.uploadservice.model.response.DependencyResponse;
import com.depscanner.uploadservice.service.impl.UploadServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller class for handling file upload and dependency analysis operations.
 */
@RestController
@RequiredArgsConstructor
@Validated
public class UploadController {

    private final UploadServiceImpl uploadService;

    /**
     * Endpoint for uploading a build tool file and parsing its open-source dependencies.
     *
     * @param buildToolFile The uploaded build tool file.
     * @return A ResponseEntity containing a list of dependency responses based on the uploaded file.
     */
    @PostMapping("/file")
    public ResponseEntity<List<DependencyResponse>> parseDependencies(@RequestPart("file") @Valid MultipartFile buildToolFile) {
        return ResponseEntity.ok(uploadService.analyseFile(buildToolFile));
    }
}
