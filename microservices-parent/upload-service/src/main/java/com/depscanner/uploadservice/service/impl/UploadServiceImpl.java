package com.depscanner.uploadservice.service.impl;

import com.depscanner.uploadservice.exception.InvalidFileException;
import com.depscanner.uploadservice.model.entity.DependencyEntity;
import com.depscanner.uploadservice.model.response.DependencyResponse;
import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import com.depscanner.uploadservice.parser.BuildToolAnalyser;
import com.depscanner.uploadservice.parser.BuildToolAnalyserFactory;
import com.depscanner.uploadservice.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * Implementation of the {@link UploadService} interface that analyses uploaded files, processes dependencies,
 * and returns the parsed dependency list as a response.
 */
@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    /**
     * Factory for creating the appropriate {@link BuildToolAnalyser} based on the uploaded files type.
     */
    private final BuildToolAnalyserFactory buildToolAnalyserFactory;

    /**
     * ModelMapper instance for mapping DependencyEntity objects to ParsedDependencyResponse Data Transfer Objects (DTO).
     */
    private final ModelMapper modelMapper;

    /**
     * Analyses the uploaded file, identifies the build tool type, and extracts dependencies using the corresponding
     * {@link BuildToolAnalyser}. The extracted dependencies are then converted to a {@link DependencyResponse}.
     *
     * @param file The MultipartFile representing the uploaded file to be analysed.
     * @return A {@link DependencyResponse} object containing information about the extracted dependencies and the result.
     * @throws InvalidFileException If the uploaded file is an unsupported type or no file is received.
     */
    @Override
    public List<DependencyResponse>  analyseFile(MultipartFile file) {
        BuildToolType buildToolType = buildToolAnalyserFactory.checkBuildTool(file);
        BuildToolAnalyser buildToolAnalyser = buildToolAnalyserFactory.getAnalyser(buildToolType);

        return switch (buildToolType) {
            case NPM, MAVEN -> getParsedDependencyList(buildToolAnalyser.analyseFile(file));
            default -> throw new InvalidFileException("Invalid file type/No file received!");
        };
    }

    /**
     * Converts a list of {@link DependencyEntity} objects to a list of {@link DependencyResponse} DTOs
     * using the {@link ModelMapper}, and encapsulates the DTOs in a {@link DependencyResponse} object.
     *
     * @param dependencies The list of {@link DependencyEntity} objects representing the dependencies to be processed.
     * @return A {@link DependencyResponse} object containing the processed dependencies.
     */
    @Override
    public List<DependencyResponse> getParsedDependencyList(Set<DependencyEntity> dependencies) {
        List<DependencyResponse> dependencyDtoList = dependencies.stream()
                .map(dependency -> modelMapper.map(dependency, DependencyResponse.class))
                .toList();

        return dependencyDtoList;
    }
}
