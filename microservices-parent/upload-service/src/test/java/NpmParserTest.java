package com.depscanner.uploadservice.parser;

import com.depscanner.uploadservice.exception.InvalidDependencyException;
import com.depscanner.uploadservice.model.entity.DependencyEntity;
import com.depscanner.uploadservice.model.enumeration.BuildToolType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class NpmParserTest {
    @Mock
    private MultipartFile multipartFile;

    private NpmParser npmParser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        npmParser = new NpmParser();
    }

    @Test
    void testGetBuildToolType() {
        BuildToolType buildToolType = npmParser.getBuildToolType();
        Assertions.assertEquals(BuildToolType.NPM, buildToolType);
    }

    @Test
    void testBuildNPMDependency() {
        String dependencyName = "dependency1";
        String version = "1.0.0";
        boolean isDevDependency = true;

        DependencyEntity dependencyEntity = npmParser.buildNPMDependency(dependencyName, version, isDevDependency);

        Assertions.assertEquals(dependencyName, dependencyEntity.getName());
        Assertions.assertEquals(version, dependencyEntity.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependencyEntity.getSystem());
        Assertions.assertEquals(isDevDependency, dependencyEntity.isDevDependency());
    }

    @Test
    void testAnalyseFileWithMissingVersionSymbol() throws IOException {
        String fileContents = "{\"dependencies\":{\"dependency1\":\"1.0.0\",\"dependency2\":\"^2.0.0\"}}";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        List<DependencyEntity> dependencyEntities = npmParser.analyseFile(multipartFile);

        assertEquals(2, dependencyEntities.size());

        DependencyEntity dependency1 = dependencyEntities.get(0);
        Assertions.assertEquals("dependency1", dependency1.getName());
        Assertions.assertEquals("1.0.0", dependency1.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency1.getSystem());
        Assertions.assertFalse(dependency1.isDevDependency());

        DependencyEntity dependency2 = dependencyEntities.get(1);
        Assertions.assertEquals("dependency2", dependency2.getName());
        Assertions.assertEquals("2.0.0", dependency2.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency2.getSystem());
        Assertions.assertFalse(dependency2.isDevDependency());
    }

    @Test
    void testBuildNPMDependencyWithNullDependencyName() {
        String version = "1.0.0";
        boolean isDevDependency = false;

        assertThrows(NullPointerException.class, () -> npmParser.buildNPMDependency(null, version, isDevDependency));
    }

    @Test
    void testBuildNPMDependencyWithEmptyDependencyName() {
        String dependencyName = "";
        String version = "1.0.0";
        boolean isDevDependency = false;

        assertThrows(InvalidDependencyException.class, () -> npmParser.buildNPMDependency(dependencyName, version, isDevDependency));
    }

    @Test
    void testBuildNPMDependencyWithEmptyVersion() {
        String dependencyName = "dependency";
        String version = "";
        boolean isDevDependency = false;

        assertThrows(InvalidDependencyException.class, () -> npmParser.buildNPMDependency(dependencyName, version, isDevDependency));
    }

    @Test
    void testAnalyseFileWithMalformedJson() throws IOException {
        String fileContents = "{";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        assertThrows(RuntimeException.class, () -> npmParser.analyseFile(multipartFile));
    }

    @Test
    void testBuildNPMDependencyWithNullName() {
        String version = "1.0.0";
        boolean isDevDependency = true;

        assertThrows(NullPointerException.class, () -> npmParser.buildNPMDependency(null, version, isDevDependency));
    }

    @Test
    public void testAnalyseFileWithDependencies() throws IOException {
        String fileContents = "{\"dependencies\":{\"dependency1\":\"1.0.0\",\"dependency2\":\"2.0.0\"},\"devDependencies\":{\"dependency3\":\"3.0.0\"}}";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        JsonObject packageJson = JsonParser.parseString(fileContents).getAsJsonObject();

        when(multipartFile.getBytes()).thenReturn(packageJson.toString().getBytes());

        List<DependencyEntity> dependencyEntities = npmParser.analyseFile(multipartFile);
        assertEquals(3, dependencyEntities.size());

        DependencyEntity dependency1 = dependencyEntities.get(0);
        Assertions.assertEquals("dependency1", dependency1.getName());
        Assertions.assertEquals("1.0.0", dependency1.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency1.getSystem());
        Assertions.assertFalse(dependency1.isDevDependency());

        DependencyEntity dependency2 = dependencyEntities.get(1);
        Assertions.assertEquals("dependency2", dependency2.getName());
        Assertions.assertEquals("2.0.0", dependency2.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency2.getSystem());
        Assertions.assertFalse(dependency2.isDevDependency());

        DependencyEntity dependency3 = dependencyEntities.get(2);
        Assertions.assertEquals("dependency3", dependency3.getName());
        Assertions.assertEquals("3.0.0", dependency3.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency3.getSystem());
        Assertions.assertTrue(dependency3.isDevDependency());
    }

    @Test
    public void testAnalyseFileWithoutDependencies() throws IOException {
        String fileContents = "{}";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        List<DependencyEntity> dependencyEntities = npmParser.analyseFile(multipartFile);

        assertEquals(0, dependencyEntities.size());
    }

    @Test
    public void testAnalyseFileWithVersionStartingWithCarat() throws IOException {
        String fileContents = "{\"dependencies\":{\"dependency1\":\"^1.0.0\"}}";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        List<DependencyEntity> dependencyEntities = npmParser.analyseFile(multipartFile);

        assertEquals(1, dependencyEntities.size());

        DependencyEntity dependency1 = dependencyEntities.get(0);
        Assertions.assertEquals("dependency1", dependency1.getName());
        Assertions.assertEquals("1.0.0", dependency1.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency1.getSystem());
        Assertions.assertFalse(dependency1.isDevDependency());
    }

    @Test
    void testAnalyseFileWithNoDependencies() throws IOException {
        String fileContents = "{}";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        List<DependencyEntity> dependencyEntities = npmParser.analyseFile(multipartFile);

        assertTrue(dependencyEntities.isEmpty());
    }

    @Test
    void testAnalyseFileWithSingleDependency() throws IOException {
        String fileContents = "{\"dependencies\":{\"dependency1\":\"1.0.0\"}}";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        List<DependencyEntity> dependencyEntities = npmParser.analyseFile(multipartFile);

        assertEquals(1, dependencyEntities.size());
        DependencyEntity dependency1 = dependencyEntities.get(0);
        Assertions.assertEquals("dependency1", dependency1.getName());
        Assertions.assertEquals("1.0.0", dependency1.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency1.getSystem());
        Assertions.assertFalse(dependency1.isDevDependency());
    }

    @Test
    void testAnalyseFileWithDevDependencies() throws IOException {
        String fileContents = "{\"devDependencies\":{\"dependency1\":\"1.0.0\",\"dependency2\":\"2.0.0\"}}";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        List<DependencyEntity> dependencyEntities = npmParser.analyseFile(multipartFile);

        assertEquals(2, dependencyEntities.size());
        DependencyEntity dependency1 = dependencyEntities.get(0);
        Assertions.assertEquals("dependency1", dependency1.getName());
        Assertions.assertEquals("1.0.0", dependency1.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency1.getSystem());
        Assertions.assertTrue(dependency1.isDevDependency());

        DependencyEntity dependency2 = dependencyEntities.get(1);
        Assertions.assertEquals("dependency2", dependency2.getName());
        Assertions.assertEquals("2.0.0", dependency2.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency2.getSystem());
        Assertions.assertTrue(dependency2.isDevDependency());
    }

    @Test
    void testAnalyseFileWithVersionPrefix() throws IOException {
        String fileContents = "{\"dependencies\":{\"dependency1\":\"~1.0.0\"}}";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        List<DependencyEntity> dependencyEntities = npmParser.analyseFile(multipartFile);

        assertEquals(1, dependencyEntities.size());
        DependencyEntity dependency1 = dependencyEntities.get(0);
        Assertions.assertEquals("dependency1", dependency1.getName());
        Assertions.assertEquals("1.0.0", dependency1.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency1.getSystem());
        Assertions.assertFalse(dependency1.isDevDependency());
    }

    @Test
    void testAnalyseFileWithEmptyFile() {
        MockMultipartFile multipartFile = new MockMultipartFile("file", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> npmParser.analyseFile(multipartFile));
    }

    @Test
    void testAnalyseFileWithMissingDependencies() throws IOException {
        String fileContents = "{\"name\":\"project-name\",\"version\":\"1.0.0\"}";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        List<DependencyEntity> dependencyEntities = npmParser.analyseFile(multipartFile);

        assertTrue(dependencyEntities.isEmpty());
    }

    @Test
    void testAnalyseFileWithMixedDependencies() throws IOException {
        String fileContents = "{\"dependencies\":{\"dependency1\":\"1.0.0\"},\"devDependencies\":{\"dependency2\":\"2.0.0\"}}";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        List<DependencyEntity> dependencyEntities = npmParser.analyseFile(multipartFile);

        assertEquals(2, dependencyEntities.size());
        DependencyEntity dependency1 = dependencyEntities.get(0);
        Assertions.assertEquals("dependency1", dependency1.getName());
        Assertions.assertEquals("1.0.0", dependency1.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency1.getSystem());
        Assertions.assertFalse(dependency1.isDevDependency());

        DependencyEntity dependency2 = dependencyEntities.get(1);
        Assertions.assertEquals("dependency2", dependency2.getName());
        Assertions.assertEquals("2.0.0", dependency2.getVersion());
        Assertions.assertEquals(BuildToolType.NPM, dependency2.getSystem());
        Assertions.assertTrue(dependency2.isDevDependency());
    }

    @Test
    void testBuildNPMDependencyWithTrimmedVersion() {
        String dependencyName = "dependency1";
        String version = "   1.0.0   ";
        boolean isDevDependency = false;

        DependencyEntity dependencyEntity = npmParser.buildNPMDependency(dependencyName, version, isDevDependency);

        Assertions.assertEquals("1.0.0", dependencyEntity.getVersion());
    }


    @Test
    public void testAnalyseFileWithInvalidFile() throws IOException {
        String fileContents = "Invalid JSON";
        when(multipartFile.getBytes()).thenReturn(fileContents.getBytes());

        assertThrows(RuntimeException.class, () -> npmParser.analyseFile(multipartFile));
    }

    @Test
    void testAnalyseFileWithIOException() throws IOException {
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        assertThrows(RuntimeException.class, () -> npmParser.analyseFile(multipartFile));
    }
}