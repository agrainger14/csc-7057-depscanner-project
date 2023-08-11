package com.depscanner.uploadservice.model.enumeration;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum BuildToolType {
    MAVEN("pom.xml"),
    NPM("package.json"),
    //future build tools
    UNKNOWN;

    private final List<String> fileExtensions;

    BuildToolType(String... fileExtensions) {
        this.fileExtensions = Arrays.asList(fileExtensions);
    }
}
