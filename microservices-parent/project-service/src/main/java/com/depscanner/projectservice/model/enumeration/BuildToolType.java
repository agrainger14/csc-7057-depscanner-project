package com.depscanner.projectservice.model.enumeration;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * BuildToolType enum to represent the BuildToolTypes for the associated files
 */
@Getter
public enum BuildToolType {
    MAVEN("pom.xml"),
    NPM("package.json"),
    UNKNOWN;

    private final List<String> fileExtensions;

    /**
     * Variable-length string used for further expansion in needed, BuildToolType may have many different types
     * of files to store dependency data.
     * @param fileExtensions String varargs
     */
    BuildToolType(String... fileExtensions) {
        this.fileExtensions = Arrays.asList(fileExtensions);
    }
}
