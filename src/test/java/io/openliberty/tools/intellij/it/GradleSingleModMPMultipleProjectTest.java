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
import java.nio.file.Paths;

/**
 * Tests Liberty Tools actions using a single module MicroProfile Gradle project with space in directory and name.
 */
public class GradleSingleModMPMultipleProjectTest extends SingleModMPProjectTestCommon {

    /**
     * Single module Microprofile project name.
     */
    private static final String SM_MP_PROJECT_NAME = "singleModGradleMP";

    private static final String MAVEN_MULTIPLE_PROJECTS_PATH = Paths.get("src", "test", "resources", "projects", "multiple-project", "singleModMavenMP").toAbsolutePath().toString();

    private static final String MULTIPLE_PROJECTS_PATH = Paths.get("src", "test", "resources", "projects", "multiple-project").toAbsolutePath().toString();
    private static final String MULTIPLE_PROJECTS_PATH_PARENT = Paths.get("src", "test", "resources", "projects").toAbsolutePath().toString();

    private static final String GRADLE_PROJECTS_PATH = Paths.get("src", "test", "resources", "projects", "gradle", "singleModGradleMP").toAbsolutePath().toString();

    private static final String MAVEN_PROJECTS_PATH = Paths.get("src", "test", "resources", "projects", "maven", "singleModMavenMP").toAbsolutePath().toString();

    private static final String GRADLE_MULTIPLE_PROJECTS_PATH = Paths.get("src", "test", "resources", "projects", "multiple-project", "singleModGradleMP").toAbsolutePath().toString();


    /**
     * Prepares the environment for test execution.
     */
    @BeforeAll
    public static void setup() {
        try {
            File theDir = new File(MULTIPLE_PROJECTS_PATH);
            System.out.println("---------------"+theDir);
            if (theDir.exists()){
                TestUtils.deleteDirectory(theDir);
                System.out.println("---------------Inside If loop--------------");
            }
            theDir.mkdirs();
            System.out.println("---------------"+theDir);

            // Copy the directory to allow renaming.
            TestUtils.copyDirectory(GRADLE_PROJECTS_PATH, GRADLE_MULTIPLE_PROJECTS_PATH);
            TestUtils.copyDirectory(MAVEN_PROJECTS_PATH, MAVEN_MULTIPLE_PROJECTS_PATH);

            // Prepare the environment with the new project path and name
            prepareEnv(MULTIPLE_PROJECTS_PATH_PARENT, SM_MP_PROJECT_NAME, true);

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
        setProjectsDirPath(MULTIPLE_PROJECTS_PATH);
        setTestReportPath(Paths.get(MULTIPLE_PROJECTS_PATH, SM_MP_PROJECT_NAME, "build", "reports", "tests", "test", "index.html"));
        setSmMPProjectName(SM_MP_PROJECT_NAME);
        setBuildCategory(BuildType.GRADLE_TYPE);
        setSmMpProjPort(9080);
        setSmMpProjResURI("api/resource");
        setSmMPProjOutput("Hello! Welcome to Open Liberty");
        setWLPInstallPath("build");
        setBuildFileName("build.gradle");
        setBuildFileOpenCommand("Liberty: View Gradle config");
        setStartParams("--hotTests");
        setStartParamsDebugPort("--libertyDebugPort=9876");
        setProjectTypeIsMultiple(true);
    }
}