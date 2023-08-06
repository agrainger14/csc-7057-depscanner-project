package com.depscanner.uploadservice.parser;

import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The BuildToolAnalyserFactory class is responsible for creating and managing instances of BuildToolAnalyser based on
 * the provided BuildToolType. It also contains a method to check the type of the build tool based on the file's
 * extension.
 */
@Component
public class BuildToolAnalyserFactory {
    private final Map<BuildToolType, BuildToolAnalyser> analyserMap;

    /**
     * Constructs a new BuildToolAnalyserFactory instance with the provided list of BuildToolAnalyser implementations.
     *
     * @param analysers A list of BuildToolAnalyser implementations to be used in the factory.
     */
    @Autowired
    public BuildToolAnalyserFactory(List<BuildToolAnalyser> analysers) {
        this.analyserMap = analysers.stream()
                .collect(Collectors.toMap(BuildToolAnalyser::getBuildToolType, Function.identity()));
    }

    /**
     * Retrieves the BuildToolAnalyser associated with the provided BuildToolType.
     *
     * @param buildToolType The BuildToolType for which the corresponding BuildToolAnalyser is to be retrieved.
     * @return The BuildToolAnalyser instance associated with the specified BuildToolType or null if not found.
     */
    public BuildToolAnalyser getAnalyser(BuildToolType buildToolType) {
        return analyserMap.get(buildToolType);
    }

    /**
     * Checks the type of the build tool based on the file's original filename extension.
     *
     * @param file The MultipartFile representing the uploaded file.
     * @return The detected BuildToolType based on the file's extension, or UNKNOWN if the extension is
     *         not recognized or the file's original filename is blank.
     */
    public BuildToolType checkBuildTool(MultipartFile file) {
        return Optional.ofNullable(file.getOriginalFilename())
                .filter(StringUtils::isNotBlank)
                .map(fileName -> Arrays.stream(BuildToolType.values())
                        .filter(buildToolType -> buildToolType.getFileExtensions().contains(fileName))
                        .findFirst()
                        .orElse(BuildToolType.UNKNOWN))
                .orElse(BuildToolType.UNKNOWN);
    }
}
