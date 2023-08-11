package com.depscanner.uploadservice.parser;

import com.depscanner.uploadservice.constant.Constant;
import com.depscanner.uploadservice.exception.InvalidDependencyException;
import com.depscanner.uploadservice.exception.InvalidFileException;
import com.depscanner.uploadservice.model.entity.DependencyEntity;
import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class MavenParserTest {
    @Mock
    private MavenXpp3Reader reader;
    private MavenParser mavenParser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        Map<String, Model> modelMap = new ConcurrentHashMap<>();
        mavenParser = new MavenParser(reader, modelMap);
    }

    @Test
    void getBuildToolTypeReturnsMaven() {
        BuildToolType buildToolType = mavenParser.getBuildToolType();
        Assertions.assertEquals(BuildToolType.MAVEN, buildToolType);
    }

    @Test
    void getParsedModelModelMapContainsModelReturnsModelFromMap() {
        Model model = new Model();
        Map<String, Model> modelMap = Collections.singletonMap(Constant.MAVEN_REPO_URL, model);
        mavenParser = new MavenParser(reader, modelMap);
        Model parsedModel = mavenParser.getParsedModel(Constant.MAVEN_REPO_URL);
        assertEquals(model, parsedModel);
    }

    @Test
    void parseMavenDependenciesReturnsDependencyEntities() {
        Model model = new Model();

        Parent parent = new Parent();
        parent.setGroupId("com.test");
        parent.setArtifactId("test-artifact");
        parent.setVersion("1.0.0");
        model.setParent(parent);

        Dependency dependency1 = new Dependency();
        dependency1.setGroupId("com.test");
        dependency1.setArtifactId("test-artifact");
        dependency1.setVersion("1.0.0");

        Dependency dependency2 = new Dependency();
        dependency2.setGroupId("com.test");
        dependency2.setArtifactId("another-test");
        dependency2.setVersion("2.0.0");

        model.setDependencies(Arrays.asList(dependency1, dependency2));

        Set<DependencyEntity> dependencies = mavenParser.parseMavenDependencies(model);

        assertNotNull(dependencies);
        assertEquals(2, dependencies.size());
    }

    @Test
    void analyseFileValidPomXmlReturnsDependencyEntities() throws IOException, XmlPullParserException {
        MultipartFile file = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);

        Parent parent = new Parent();
        parent.setGroupId("com.example");
        parent.setArtifactId("parent-artifact");
        parent.setVersion("1.0.0");

        Model model = new Model();
        model.setParent(parent);
        Dependency dependency = new Dependency();
        dependency.setGroupId("com.example");
        dependency.setArtifactId("example-artifact");
        dependency.setVersion("1.0.0");
        model.setDependencies(Collections.singletonList(dependency));

        when(file.getInputStream()).thenReturn(inputStream);
        when(reader.read(inputStream)).thenReturn(model);

        Set<DependencyEntity> dependencies = mavenParser.analyseFile(file);

        assertNotNull(dependencies);
        assertEquals(1, dependencies.size());
    }

    @Test
    void buildMavenDependencyValidDependencyReturnsDependencyEntity() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("group1");
        dependency.setArtifactId("artifact1");
        dependency.setVersion("1.0");

        DependencyEntity result = mavenParser.buildMavenDependency(dependency);

        Assertions.assertEquals("group1:artifact1", result.getName());
        Assertions.assertEquals(BuildToolType.MAVEN, result.getSystem());
        Assertions.assertEquals("1.0", result.getVersion());
    }

    @Test
    void buildMavenDependencyMissingVersionReturnsDependencyEntityWithNullVersion() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("group1");
        dependency.setArtifactId("artifact1");

        DependencyEntity result = mavenParser.buildMavenDependency(dependency);

        Assertions.assertEquals("group1:artifact1", result.getName());
        Assertions.assertEquals(BuildToolType.MAVEN, result.getSystem());
        assertNull(result.getVersion());
    }

    @Test
    void buildMavenDependencyNullDependencyThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> mavenParser.buildMavenDependency(null));
    }

    @Test
    void analyseFileInvalidPomXmlThrowsInvalidPomFileException() throws IOException, XmlPullParserException {
        MockMultipartFile file = mock(MockMultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        when(file.getInputStream()).thenReturn(inputStream);
        when(reader.read(inputStream)).thenThrow(XmlPullParserException.class);

        assertThrows(InvalidFileException.class, () -> mavenParser.analyseFile(file));
    }

    @Test
    void analyseFileIOExceptionThrowsIllegalArgumentException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getInputStream()).thenThrow(new IOException());

        assertThrows(InvalidFileException.class, () -> mavenParser.analyseFile(file));
    }

    @Test
    void getParsedModelInvalidApiUrlThrowsIllegalArgumentException() {
        String apiUrl = "invalid-url";
        assertThrows(IllegalArgumentException.class, () -> mavenParser.getParsedModel(apiUrl));
    }

    @Test
    void parseMavenDependenciesVersionWithPlaceholderSetsVersionToNull() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("group1");
        dependency.setArtifactId("artifact1");
        dependency.setVersion("${unresolved.version}");

        Model model = mock(Model.class);
        when(model.getDependencies()).thenReturn(Collections.singletonList(dependency));
        when(model.getProperties()).thenReturn(new Properties());

        Set<DependencyEntity> result = mavenParser.parseMavenDependencies(model);

        assertNotNull(result);
        assertEquals(1, result.size());

        DependencyEntity entity = result.iterator().next();
        assertNull(entity.getVersion());
    }

    @Test
    void buildMavenDependencyEmptyGroupIdThrowsInvalidDependencyException() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("");
        dependency.setArtifactId("artifact1");
        dependency.setVersion("1.0");

        assertThrows(InvalidDependencyException.class, () -> mavenParser.buildMavenDependency(dependency));
    }

    @Test
    void buildMavenDependencyEmptyArtifactIdThrowsInvalidDependencyException() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("group1");
        dependency.setArtifactId("");
        dependency.setVersion("1.0");

        assertThrows(InvalidDependencyException.class, () -> mavenParser.buildMavenDependency(dependency));
    }

    @Test
    void buildMavenDependencyCreatesDependencyEntity() {
        Dependency dependency = new Dependency();
        dependency.setGroupId("group1");
        dependency.setArtifactId("artifact1");
        dependency.setVersion("1.0");

        DependencyEntity result = mavenParser.buildMavenDependency(dependency);

        Assertions.assertEquals("group1:artifact1", result.getName());
        Assertions.assertEquals(BuildToolType.MAVEN, result.getSystem());
        Assertions.assertEquals("1.0", result.getVersion());
    }

    @Test
    void buildApiUrlBuildsCorrectApiUrl() {
        String apiUrl = mavenParser.buildApiUrl("group1", "artifact1", "1.0");
        assertEquals(Constant.MAVEN_REPO_URL + "group1/artifact1/1.0/artifact1-1.0.pom", apiUrl);
    }

    @Test
    void parseModelInvalidApiUrlThrowsIllegalArgumentException() {
        String invalidApiUrl = "invalid-url";
        assertThrows(IllegalArgumentException.class, () -> mavenParser.parseModel(invalidApiUrl));
    }
}
