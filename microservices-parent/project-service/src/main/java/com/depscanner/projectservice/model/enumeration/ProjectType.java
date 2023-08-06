package com.depscanner.projectservice.model.enumeration;

import com.depscanner.projectservice.exception.UnsupportedBuildToolException;
import lombok.Getter;

/**
 * ProjectType Enum to represent Programming Languages.
 */
@Getter
public enum ProjectType {
    JAVA("Java"),
    JAVASCRIPT("JavaScript");

    private final String languageName;

    ProjectType(String displayName) {
        this.languageName = displayName;
    }

    /**
     * This method takes the BuildToolType enum and converts it to its appropriate programming language
     * @return ProjectType (Programming Language)
     */
    public static ProjectType fromSystem(BuildToolType buildToolType) {
        return switch (buildToolType) {
            case MAVEN -> JAVA;
            case NPM -> JAVASCRIPT;
            default -> throw new UnsupportedBuildToolException("Unsupported build tool type: " + buildToolType);
        };
    }
}
