package com.depscanner.uploadservice.service;

import com.depscanner.uploadservice.model.entity.DependencyEntity;
import com.depscanner.uploadservice.model.response.DependencyResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * The UploadService interface defines the contract for analysing uploaded files and processing dependencies.
 * It provides methods to analyse the uploaded file, extract dependencies, and retrieve the parsed dependency list.
 */
public interface UploadService {

    /**
     * Analyses the uploaded file and extracts the dependencies from it.
     *
     * @param file The MultipartFile representing the uploaded file to be analysed.
     * @return A DependencyResponse object containing information about the extracted dependencies and the result.
     */
    List<DependencyResponse>  analyseFile(MultipartFile file);

    /**
     * Converts a list of DependencyEntity objects to a DependencyResponse object for response.
     *
     * @param dependencies The list of DependencyEntity objects representing the dependencies to be processed.
     * @return A DependencyResponse object containing the processed dependencies.
     */
    List<DependencyResponse>  getParsedDependencyList(Set<DependencyEntity> dependencies);
}
