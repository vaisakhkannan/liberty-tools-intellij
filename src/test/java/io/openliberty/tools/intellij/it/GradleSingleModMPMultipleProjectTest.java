/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.tools.intellij.it;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Tests Liberty Tools actions using a single module MicroProfile Gradle project with space in directory and name.
 */
public class GradleSingleModMPMultipleProjectTest extends SingleModMPMultipleProjectTestCommon {

    /**
     * Single module Microprofile project name.
     */
    private static final String SM_MP_GRADLE_PROJECT_NAME = "singleModGradleMP";

    /**
     * Single module Microprofile project name.
     */
    private static final String SM_MP_MAVEN_PROJECT_NAME = "singleModMavenMP";

    String[] projects = {SM_MP_GRADLE_PROJECT_NAME, SM_MP_MAVEN_PROJECT_NAME};

    String[] config = {"Liberty: View Gradle config", "Liberty: View pom.xml"};
    String[] configFiles = {"build.gradle", "pom.xml"};

    private static final String MAVEN_MULTIPLE_PROJECTS_PATH = Paths.get("src", "test", "resources", "projects", "multiple-project", "maven-project").toAbsolutePath().toString();

    private static final String MULTIPLE_PROJECTS_PATH = Paths.get("src", "test", "resources", "projects", "multiple-project").toAbsolutePath().toString();
    private static final String MULTIPLE_PROJECTS_PATH_PARENT = Paths.get("src", "test", "resources", "projects").toAbsolutePath().toString();

    private static final String GRADLE_PROJECTS_PATH = Paths.get("src", "test", "resources", "projects", "gradle", "singleModGradleMP").toAbsolutePath().toString();

    private static final String MAVEN_PROJECTS_PATH = Paths.get("src", "test", "resources", "projects", "maven", "singleModMavenMP").toAbsolutePath().toString();

    private static final String GRADLE_MULTIPLE_PROJECTS_PATH = Paths.get("src", "test", "resources", "projects", "multiple-project", "gradle-project").toAbsolutePath().toString();

    String[] projectDirPathArray = {GRADLE_MULTIPLE_PROJECTS_PATH, MAVEN_MULTIPLE_PROJECTS_PATH};

    String[] wlpInstallPathArray ={"build", Paths.get("target", "liberty").toString()};

    String[] startParamsArray ={"--hotTests", "-DhotTests=true"};

    /**
     * Prepares the environment for test execution.
     */
    @BeforeAll
    public static void setup() {
        try {
            File theDir = new File(MULTIPLE_PROJECTS_PATH);
            if (!theDir.exists()){
                theDir.mkdirs();
            }

            // Copy the directory to allow renaming.
            TestUtils.copyDirectory(GRADLE_PROJECTS_PATH, GRADLE_MULTIPLE_PROJECTS_PATH);
            TestUtils.copyDirectory(MAVEN_PROJECTS_PATH, MAVEN_MULTIPLE_PROJECTS_PATH);

            // Prepare the environment with the new project path and name
            prepareEnv(MULTIPLE_PROJECTS_PATH_PARENT, SM_MP_GRADLE_PROJECT_NAME);

        } catch (IOException e) {
            System.err.println("Setup failed: " + e.getMessage());
            e.printStackTrace();
            Assertions.fail("Test setup failed due to an IOException: " + e.getMessage());
        }
    }

    /**
     * Cleanup includes deleting the created project path.
     */
    @AfterAll
    public static void cleanup() {
        try {
            closeProjectView();
        } finally {
            deleteDirectoryIfExists(MULTIPLE_PROJECTS_PATH);
        }
    }

    GradleSingleModMPMultipleProjectTest() {
        // set the new locations for the test, not the original locations
        setProjectsDirPath(GRADLE_MULTIPLE_PROJECTS_PATH);
        setProjectsDirPathArray(projectDirPathArray);
        setTestReportPath(Paths.get(GRADLE_MULTIPLE_PROJECTS_PATH, "build", "reports", "tests", "test", "index.html"));
//        setSmMPProjectName(SM_MP_GRADLE_PROJECT_NAME);
        setSmMPProjectNameArray(projects);
        setSmMpProjPort(9080);
        setSmMpProjResURI("api/resource");
        setSmMPProjOutput("Hello! Welcome to Open Liberty");
//        setWLPInstallPath("build");
        setWLPInstallPathArray(wlpInstallPathArray);
//        setBuildFileName("build.gradle");
        setBuildFileNameArray( configFiles);
        setBuildFileOpenCommandArray(config);
//        setStartParams("--hotTests");
        setStartParamsArray(startParamsArray);
        setStartParamsDebugPort("--libertyDebugPort=9876");
    }
}