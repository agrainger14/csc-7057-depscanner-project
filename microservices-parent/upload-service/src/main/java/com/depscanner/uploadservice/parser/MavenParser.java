package com.depscanner.uploadservice.parser;

import com.depscanner.uploadservice.constant.Constant;
import com.depscanner.uploadservice.exception.InvalidDependencyException;
import com.depscanner.uploadservice.exception.InvalidFileException;
import com.depscanner.uploadservice.exception.NoDependenciesParsedException;
import com.depscanner.uploadservice.model.entity.DependencyEntity;
import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A component that implements the BuildToolParser interface to parse Maven pom (Project Object Model) files
 * and extract the dependencies listed in them. It also handles resolving dependency versions from the parent pom
 * (if possible) and parsing the properties defined in the pom file.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MavenParser implements BuildToolParser {
    /**
     * The MavenXpp3Reader instance used to read Maven pom files in XML format.
     */
    private final MavenXpp3Reader reader;

    /**
     * A Map used to cache parsed Model instances based on the Maven API urls
     * for better performance when resolving parent pom.
     */
    private final Map<String, Model> modelMap;

    /**
     * Parses the provided Maven pom file in XML format to extract its dependencies.
     *
     * @param file The MultipartFile representing the Maven pom file to be analyzed.
     * @return A list of DependencyEntity objects representing the extracted dependencies from the pom file.
     * @throws InvalidFileException If the provided file is not a valid pom.xml file.
     *                              or if an IOException occurs while reading the pom.xml file.
     */
    @Override
    public Set<DependencyEntity> analyseFile(MultipartFile file) {
        validateFile(file);

        try (InputStream inputStream = file.getInputStream()) {
            Model model = reader.read(inputStream);
            return parseMavenDependencies(model);
        } catch (XmlPullParserException e) {
            throw new InvalidFileException("The provided file is not a valid pom.xml file");
        } catch (IOException e) {
            throw new InvalidFileException("IOException occurred in pom.xml file");
        }
    }

    /**
     * Returns the BuildToolType enum value representing Maven.
     *
     * @return BuildToolType.MAVEN
     */
    @Override
    public BuildToolType getBuildToolType() {
        return BuildToolType.MAVEN;
    }

    /**
     * Validates the provided MultipartFile representing the Maven pom file.
     *
     * @param file The MultipartFile representing the Maven pom file to be validated.
     * @throws InvalidFileException If the provided file is null or empty.
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Pom.xml file is null or empty.");
        }
    }

    /**
     * Retrieves a cached parsed Model instance based on the given maven repo link or parses a new Model if not already cached.
     *
     * @param apiUrl The API URL representing the Maven pom to be parsed.
     * @return The parsed Model instance representing the Maven pom or null if parsing fails.
     */
    public Model getParsedModel(String apiUrl) {
        if (modelMap.containsKey(apiUrl)) {
            return modelMap.get(apiUrl);
        } else {
            Model parsedModel = parseModel(apiUrl);
            if (parsedModel != null) {
                modelMap.put(apiUrl, parsedModel);
            }
            return parsedModel;
        }
    }

    /**
     * Parses the Maven pom model to extract its dependencies and handle version placeholders if present.
     *
     * @param model The Maven Model instance representing the pom to be parsed.
     * @return A list of DependencyEntity objects representing the extracted dependencies from the pom model.
     * @throws NoDependenciesParsedException If no dependencies are found in the provided pom model.
     */
    public Set<DependencyEntity> parseMavenDependencies(Model model) {
        Set<DependencyEntity> dependencies = Optional.ofNullable(model.getDependencies())
                .map(dep -> dep.stream()
                        .map(this::buildMavenDependency)
                        .peek(dependency -> handleVersionPlaceholder(dependency, model))
                        .collect(Collectors.toSet())
                )
                .orElse(Collections.emptySet());

        if (dependencies.isEmpty()) {
            throw new NoDependenciesParsedException("No dependencies found in the uploaded pom.xml file");
        }

        return dependencies;
    }

    /**
     * Handles the version placeholders in the dependency version and resolves them using properties defined in the pom
     * if available.
     *
     * @param dependency - the dependency with a version that needs to be retrieved
     * @param model - the model which contains the version within the properties
     */
    private void handleVersionPlaceholder(DependencyEntity dependency, Model model) {
        Properties properties = model.getProperties();

        String versionProperty = extractVersionProperty(dependency.getVersion());

        if (versionProperty != null) {
            dependency.setVersion(properties.getProperty(versionProperty));
            return;
        }

        if (dependency.getVersion() == null || !containsVersionPlaceholder(dependency.getVersion())) {
            getDependencyVersion(dependency, model);
        }
    }

    /**
     * Extracts the version property from the version string if it is in the format ${...}.
     *
     * @param version The version string to be checked.
     * @return The extracted version property if present, or null if not.
     */
    private String extractVersionProperty(String version) {
        if (version != null && version.startsWith("${") && version.endsWith("}")) {
            return version.substring(2, version.length() - 1);
        } else {
            return null;
        }
    }

    /**
     * Checks if the provided version contains placeholders like ${...} (for properties etc).
     *
     * @param version The version string to be checked.
     * @return true if the version contains placeholders, false otherwise.
     */
    private boolean containsVersionPlaceholder(String version) {
        return version != null && (version.contains("$") || version.contains("{") || version.contains("}"));
    }

    /**
     * Resolves the version of the dependency by searching for it in the parent pom and its managed dependencies.
     * It also resolves property and import scoped versions by looking up the version from the pom properties/scope.
     * @param dependency - the dependency with the version to get
     * @param model - the initial model parent from the file upload
     */
    public void getDependencyVersion(DependencyEntity dependency, Model model) {
        Parent parent = model.getParent();
        String parentGroupId = parent.getGroupId();
        String parentArtifactId = parent.getArtifactId();
        String parentVersion = parent.getVersion();

        String[] dependencyNameSplit = dependency.getName().split(":");
        String dependencyGroupId = dependencyNameSplit[0];
        String dependencyArtifactId = dependencyNameSplit[1];


        Model parentModel = getParsedModel(buildApiUrl(parentGroupId,  parentArtifactId, parentVersion));

        //no model available - failed to retrieve from maven repo (private model, custom user model etc).
        if (parentModel == null) {
            return;
        }

        while (parentModel.getParent() != null) {
            Parent responseParent = parentModel.getParent();
            String parentUrl = buildApiUrl(responseParent.getGroupId(), responseParent.getArtifactId(), responseParent.getVersion());
            parentModel = getParsedModel(parentUrl);
        }

        if (model.getDependencyManagement() != null) {
            for (Dependency dependencyInManager : model.getDependencyManagement().getDependencies()) {
                if (dependencyInManager.getGroupId().equals(dependencyGroupId)) {
                    String version = extractVersionProperty(dependencyInManager.getVersion());

                    if (version != null) {
                        parentGroupId = dependencyInManager.getGroupId();
                        parentArtifactId = dependencyInManager.getArtifactId();
                        parentVersion = model.getProperties().getProperty(version);
                        String importedParentUrl = buildApiUrl(parentGroupId, parentArtifactId, parentVersion);
                        Model importedParentModel = getParsedModel(importedParentUrl);

                        if (importedParentModel != null) {
                            parentModel = importedParentModel;
                        }
                    }
                }
            }
        } else {
            //check if an "import" scope is present, if so need to update the parent model.
            Optional<Dependency> dependencyScope = findImportedDependency(parentModel, dependencyGroupId);

            //imports versions from that pom, we need to look this up and update the parentModel.
            if (dependencyScope.isPresent()) {
                String importedVersion = parentModel.getProperties().getProperty(extractVersionProperty(dependencyScope.get().getVersion()));
                String importedParentUrl = buildApiUrl(dependencyScope.get().getGroupId(), dependencyScope.get().getArtifactId(), importedVersion);
                Model importedParentModel = getParsedModel(importedParentUrl);

                //update parentModel
                if (importedParentModel != null) {
                    parentModel = importedParentModel;
                }
            }
        }

        //find the dependency by the parentModel, if the dependency had an "import" scope, the imported pom is now the parentModel.
        Optional<Dependency> matchingDependency = findDependency(parentModel, dependencyGroupId, dependencyArtifactId);

        matchingDependency.ifPresent(managedDependency -> dependency.setVersion(managedDependency.getVersion()));

        //resolve version from properties
        Optional.ofNullable(dependency.getVersion())
                .map(this::extractVersionProperty)
                .map(parentModel.getProperties()::getProperty)
                .ifPresent(dependency::setVersion);

        if (Objects.equals(dependency.getVersion(), "${project.version}" )) {
            dependency.setVersion(parentModel.getVersion());
        }
    }


    /**
     * Finds a specific dependency by its groupId and artifactId within the given parent model.
     *
     * @param parentModel The parent model in which the dependencies are referenced.
     * @param groupId     The groupId of the dependency to be searched.
     * @param artifactId  The artifactId of the dependency to be searched.
     * @return Optional containing the found Dependency if it exists, or empty Optional if not found.
     */
    private Optional<Dependency> findDependency(Model parentModel, String groupId, String artifactId) {
        if (parentModel.getDependencyManagement() != null) {
            return parentModel.getDependencyManagement().getDependencies().stream()
                    .filter(dependencyFilter ->
                            Objects.equals(dependencyFilter.getArtifactId(), artifactId)
                                    && Objects.equals(dependencyFilter.getGroupId(), groupId))
                    .findFirst();
        } else {
            return parentModel.getDependencies().stream()
                    .filter(dependencyFilter ->
                            Objects.equals(dependencyFilter.getArtifactId(), artifactId)
                                    && Objects.equals(dependencyFilter.getGroupId(), groupId))
                    .findFirst();
        }
    }

    /**
     * Finds an imported dependency by its groupId within the given parent model.
     *
     * @param parentModel The parent model in which the imported dependencies are referenced.
     * @param groupId     The groupId of the imported dependency to be searched.
     * @return Optional containing the found Dependency if it exists, or empty Optional if not found.
     */
    private Optional<Dependency> findImportedDependency(Model parentModel, String groupId) {
        final String scope = "import";

        if (parentModel.getDependencyManagement() == null) {
            return Optional.empty();
        }

        return parentModel.getDependencyManagement().getDependencies().stream()
                .filter(dependencyFilter ->
                        Objects.equals(dependencyFilter.getGroupId(), groupId)
                                && Objects.equals(dependencyFilter.getScope(), scope))
                .findFirst();
    }


    /**
     * Builds the API URL to fetch the Maven pom file from the Maven repo based on the groupId, artifactId, and version.
     *
     * @param groupId    The Maven project's groupId.
     * @param artifactId The Maven project's artifactId.
     * @param version    The Maven project's version.
     * @return The url to fetch the pom file from the Maven Repo.
     */
    public String buildApiUrl(String groupId, String artifactId, String version)  {
        return String.format("%s%s/%s/%s/%s-%s.pom",
                Constant.MAVEN_REPO_URL, groupId.replace('.', '/'), artifactId, version, artifactId, version);
    }

    /**
     * Parses the Maven pom model from the provided API url.
     *
     * @param apiUrl The url representing the Maven pom to be parsed.
     * @return The parsed Model instance representing the Maven POM or null if parsing fails.
     */
    public Model parseModel(String apiUrl) {
        try (InputStream inputStream = new URI(apiUrl).toURL().openStream()) {
            return reader.read(inputStream);
        } catch (URISyntaxException | XmlPullParserException e) {
            log.error("Error parsing model from url: {}", apiUrl);
        } catch (FileNotFoundException e) {
            log.error("POM file not found at maven repo: {}", apiUrl);
        } catch (IOException e) {
            log.error("Error reading model from maven repo: {}", apiUrl);
        }
        return null;
    }

    /**
     * Builds a DependencyEntity object representing a Maven dependency from the provided Maven Dependency object.
     *
     * @param dependency The Maven Dependency object to be converted to a DependencyEntity object.
     * @return The DependencyEntity object representing the Maven dependency.
     * @throws InvalidDependencyException If the dependency's name, groupId, or artifactId is empty.
     */
    public DependencyEntity buildMavenDependency(Dependency dependency) {
        if (dependency.getArtifactId().isEmpty() || dependency.getGroupId().isEmpty()) {
            throw new InvalidDependencyException("Dependency name cannot be empty");
        }

        return DependencyEntity.builder()
                .name(dependency.getGroupId() + ":" + dependency.getArtifactId())
                .system(this.getBuildToolType())
                .version(dependency.getVersion())
                .build();
    }
}
