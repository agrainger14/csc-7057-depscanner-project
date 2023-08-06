package com.depscanner.uploadservice.parser;

import com.depscanner.uploadservice.model.entity.DependencyEntity;
import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * The BuildToolAnalyser Interface defines the contract used by parsers to implement core functionality.
 */
public interface BuildToolAnalyser {
    /**
     * Used to analyse the uploaded file and returns a list of the parsed dependency entities
     *
     * @param file the uploaded file
     * @return List of dependency entities
     */
    Set<DependencyEntity> analyseFile(MultipartFile file);

    /**
     * Get method to return the BuildToolType enum
     * @return BuildToolType
     */
    BuildToolType getBuildToolType();
}
