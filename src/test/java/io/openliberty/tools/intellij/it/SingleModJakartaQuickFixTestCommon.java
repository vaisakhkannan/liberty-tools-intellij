/*******************************************************************************
 * Copyright (c) 2023, 2025 IBM Corporation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.tools.intellij.it;

import com.automation.remarks.junit5.Video;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.JTreeFixture;
import io.openliberty.tools.intellij.it.fixtures.ProjectFrameFixture;
import org.junit.jupiter.api.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitForIgnoringError;

public abstract class SingleModJakartaQuickFixTestCommon {
    public static final String REMOTEBOT_URL = "http://localhost:8082";
    public static final RemoteRobot remoteRobot = new RemoteRobot(REMOTEBOT_URL);

    String projectName;
    String projectsPath;

    public SingleModJakartaQuickFixTestCommon(String projectName, String projectsPath) {
        this.projectName = projectName;
        this.projectsPath = projectsPath;
    }

    /**
     * Processes actions before each test.
     *
     * @param info Test information.
     */
    @BeforeEach
    public void beforeEach(TestInfo info) {
        TestUtils.printTrace(TestUtils.TraceSevLevel.INFO, this.getClass().getSimpleName() + "." + info.getDisplayName() + ". Entry");
    }

    /**
     * Processes actions after each test.
     *
     * @param info Test information.
     */
    @AfterEach
    public void afterEach(TestInfo info) {
        TestUtils.printTrace(TestUtils.TraceSevLevel.INFO, this.getClass().getSimpleName() + "." + info.getDisplayName() + ". Exit");
        TestUtils.detectFatalError();
    }

    /**
     * Cleanup.
     */
    @AfterAll
    public static void cleanup() {
        if (remoteRobot.isMac()) {
            UIBotTestUtils.closeAllEditorTabs(remoteRobot);
        }
        else {
            UIBotTestUtils.runActionFromSearchEverywherePanel(remoteRobot, "Close All Tabs", 3);
        }
        UIBotTestUtils.closeProjectView(remoteRobot);
        UIBotTestUtils.closeProjectFrame(remoteRobot);
        UIBotTestUtils.validateProjectFrameClosed(remoteRobot);
    }

    /**
     * Tests Jakarta Language Server quick fix support in a Java source file
     */
    @Test
    @Video
    public void testJakartaQuickFixInJsonbTransientDiagnosticClass() {
        String codeWithDiagnostic = "@JsonbProperty\\(\"fav_lang\"\\)\\s*" +
                "@JsonbAnnotation\\s*" +
                "@JsonbTransient\\s*" +
                "private\\s+String\\s+favoriteLanguage;";
        String codeAfterQuickfix = "@JsonbProperty\\(\"fav_lang\"\\)\\s*" +
                "@JsonbAnnotation\\s*" +
                "private\\s+String\\s+favoriteLanguage;";
        String flaggedString = "favoriteLanguage";

        String expectedHoverData = "When a class field is annotated with @JsonbTransient, this field, getter or setter must not be annotated with other JSON Binding annotations.";

        ProjectFrameFixture projectFrame = remoteRobot.find(ProjectFrameFixture.class, Duration.ofMinutes(2));
        JTreeFixture projTree = projectFrame.getProjectViewJTree(projectName);

        projTree.expand(projectName, "src", "main", "java", "io.openliberty.sample.jakarta", "jsonb");
        UIBotTestUtils.openFile(remoteRobot, projectName, "JsonbTransientDiagnostic", projectName, "src", "main", "java", "io.openliberty.sample.jakarta", "jsonb");

        Path pathToSrc = Paths.get(projectsPath, projectName, "src", "main", "java", "io", "openliberty", "sample", "jakarta", "jsonb", "JsonbTransientDiagnostic.java");
        String quickfixChooserString = "Remove @JsonbTransient";

        // get focus on file tab prior to copy
        UIBotTestUtils.clickOnFileTab(remoteRobot, "JsonbTransientDiagnostic.java");

        try {
            // Validates if 'codeWithDiagnostic' snippet exists in the file at 'pathToSrc'.
            UIBotTestUtils.validateCodeSnippet(pathToSrc.toString(), codeWithDiagnostic);

            //there should be a diagnostic for "private" on method signature - move cursor to hover point
            UIBotTestUtils.hoverInAppServerCfgFile(remoteRobot, flaggedString, "JsonbTransientDiagnostic.java", UIBotTestUtils.PopupType.DIAGNOSTIC);

            String foundHoverData = UIBotTestUtils.getHoverStringData(remoteRobot, UIBotTestUtils.PopupType.DIAGNOSTIC);
            TestUtils.validateHoverData(expectedHoverData, foundHoverData);

            //there should be a diagnostic - move cursor to hover point
            UIBotTestUtils.hoverForQuickFixInAppFile(remoteRobot, flaggedString, "JsonbTransientDiagnostic.java", quickfixChooserString);

            // trigger and use the quickfix popup attached to the diagnostic
            UIBotTestUtils.chooseQuickFix(remoteRobot, quickfixChooserString);

            // Validates if 'codeAfterQuickfix' snippet exists in the file at 'pathToSrc'.
            UIBotTestUtils.validateCodeSnippet(pathToSrc.toString(), codeAfterQuickfix);
        }
        finally {
            // Undo modified content.
            UIBotTestUtils.undoWindowContent(remoteRobot);
        }
    }

    /**
     * Prepares the environment to run the tests.
     *
     * @param projectPath The path of the project.
     * @param projectName The name of the project being used.
     */

    public static void prepareEnv(String projectPath, String projectName) {
        waitForIgnoringError(Duration.ofMinutes(4), Duration.ofSeconds(5), "Wait for IDE to start", "IDE did not start", () -> remoteRobot.callJs("true"));
        UIBotTestUtils.findWelcomeFrame(remoteRobot);

        UIBotTestUtils.importProject(remoteRobot, projectPath, projectName);
        UIBotTestUtils.openProjectView(remoteRobot);
        // IntelliJ does not start building and indexing until the Project View is open
        UIBotTestUtils.waitForIndexing(remoteRobot);
        UIBotTestUtils.openAndValidateLibertyToolWindow(remoteRobot, projectName);
        UIBotTestUtils.closeLibertyToolWindow(remoteRobot);

        // Removes the build tool window if it is opened. This prevents text to be hidden by it.
        UIBotTestUtils.removeToolWindow(remoteRobot, "Build:");
    }
}