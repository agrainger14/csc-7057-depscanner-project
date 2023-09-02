package com.depscanner.uploadservice.model.enumeration;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Enumerates the build tool types used in the application.
 */
@Getter
public enum BuildToolType {
    /**
     * Represents the Maven build tool with its associated file extension.
     */
    MAVEN("pom.xml"),
    /**
     * Represents an unknown or unsupported build tool type.
     */
    NPM("package.json"),
    /**
     * Represents an unknown or unsupported build tool type.
     */
    UNKNOWN;

    /**
     * The list of file extensions associated with the build tool type.
     */
    private final List<String> fileExtensions;

    /**
     * Constructor for the BuildToolType enum with associated file extensions.
     *
     * @param fileExtensions The file extensions associated with the build tool type.
     */
    BuildToolType(String... fileExtensions) {
        this.fileExtensions = Arrays.asList(fileExtensions);
    }
}
