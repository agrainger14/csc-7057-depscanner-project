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

@RestController
@RequiredArgsConstructor
@Validated
public class UploadController {

    private final UploadServiceImpl uploadService;

    @PostMapping("/file")
    public ResponseEntity<List<DependencyResponse>> parseDependencies(@RequestPart("file") @Valid MultipartFile buildToolFile) {
        return ResponseEntity.ok(uploadService.analyseFile(buildToolFile));
    }
}
