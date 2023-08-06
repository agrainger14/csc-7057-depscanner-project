package com.depscanner.uploadservice.parser;

import com.depscanner.uploadservice.exception.InvalidDependencyException;
import com.depscanner.uploadservice.exception.InvalidFileException;
import com.depscanner.uploadservice.model.entity.DependencyEntity;
import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A component that implements the BuildToolAnalyser interface to parse NPM package.json files
 * and extract the dependencies and devDependencies listed in them.
 * It converts the parsed dependencies into DependencyEntity objects to be associated to a user project if required.
 */
@Component
public class NpmParser implements BuildToolAnalyser {

    /**
     * Parses the provided NPM package.json file and extracts dependencies and labels devDependencies.
     *
     * @param file The MultipartFile representing the NPM package.json file to be analyzed.
     * @return A list of DependencyEntity objects representing the extracted dependencies from the package.json file.
     * @throws InvalidFileException If the provided JSON file is empty or contains incorrect syntax
     *                              or if an I/O error occurs while reading the package.json file.
     */
    @Override
    public Set<DependencyEntity> analyseFile(MultipartFile file) {
        Set<DependencyEntity> dependencyEntities = new HashSet<>();

        try {
            String fileContents = new String(file.getBytes());

            if (fileContents.isEmpty()) {
                throw new IllegalArgumentException("Empty JSON file received!");
            }

            JsonObject packageJson = JsonParser.parseString(fileContents).getAsJsonObject();

            if (packageJson.has("dependencies")) {
                JsonObject dependencies = packageJson.getAsJsonObject("dependencies");
                parseDependencies(dependencies, dependencyEntities, false);
            }

            if (packageJson.has("devDependencies")) {
                JsonObject devDependencies = packageJson.getAsJsonObject("devDependencies");
                parseDependencies(devDependencies, dependencyEntities, true);
            }

        } catch (IOException ex) {
            throw new InvalidFileException("IO Exception occurred while reading JSON file");
        } catch (JsonSyntaxException ex) {
            throw new InvalidFileException("Incorrect JSON File Format");
        }

        return dependencyEntities;
    }

    /**
     * Parses the dependencies or devDependencies section in the package.json and creates DependencyEntity objects.
     *
     * @param dependencies The JsonObject representing the dependencies or devDependencies section in package.json.
     * @param dependencyEntities The list to which the DependencyEntity objects will be added.
     * @param isDevDependency A boolean indicating whether the parsed dependencies are devDependencies (true) or dependencies (false).
     */
    private void parseDependencies(JsonObject dependencies, Set<DependencyEntity> dependencyEntities, boolean isDevDependency) {
        for (String dependencyName : dependencies.keySet()) {
            String version = dependencies.get(dependencyName).getAsString();
            if (version.startsWith("^") || version.startsWith("~")) {
                version = version.substring(1);
            }
            DependencyEntity dependencyEntity = buildNPMDependency(dependencyName, version, isDevDependency);
            dependencyEntities.add(dependencyEntity);
        }
    }

    /**
     * Returns the BuildToolType enum value representing NPM.
     *
     * @return BuildToolType.NPM
     */
    @Override
    public BuildToolType getBuildToolType() {
        return BuildToolType.NPM;
    }

    /**
     * Builds a DependencyEntity object representing an NPM dependency.
     *
     * @param dependencyName   The name of the NPM dependency.
     * @param version          The version of the NPM dependency.
     * @param isDevDependency  A boolean indicating whether the dependency is a devDependency (true) or not (false).
     * @return The DependencyEntity object representing the NPM dependency.
     * @throws InvalidDependencyException If the dependencyName or version is empty.
     */
    public DependencyEntity buildNPMDependency(String dependencyName, String version, boolean isDevDependency) {
        if (dependencyName.isEmpty()) {
            throw new InvalidDependencyException("Dependency name cannot be empty");
        }
        if (version.isEmpty()) {
            throw new InvalidDependencyException("Version cannot be empty");
        }

        return DependencyEntity.builder()
                .name(dependencyName)
                .version(version.trim())
                .system(this.getBuildToolType())
                .isDevDependency(isDevDependency)
                .build();
    }
}
