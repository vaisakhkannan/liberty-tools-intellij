package io.openliberty.tools.intellij.it;

import com.automation.remarks.junit5.Video;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.ContainerFixture;
import com.intellij.remoterobot.fixtures.JTreeFixture;
import com.intellij.remoterobot.utils.RepeatUtilsKt;
import io.openliberty.tools.intellij.it.fixtures.ProjectFrameFixture;
import io.openliberty.tools.intellij.it.fixtures.WelcomeFrameFixture;
import org.junit.jupiter.api.*;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitForIgnoringError;
import static com.intellij.util.containers.ContainerUtil.findAll;

public abstract class AboutFindTestCommon {
    public static final String REMOTEBOT_URL = "http://localhost:8082";
    public static final RemoteRobot remoteRobot = new RemoteRobot(REMOTEBOT_URL);

    String projectName;
    String projectsPath;


    public AboutFindTestCommon(String projectName, String projectsPath) {
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
    }

    /**
     * Cleanup.
     */
    @AfterAll
    public static void cleanup() {
//        ProjectFrameFixture projectFrame = remoteRobot.find(ProjectFrameFixture.class, Duration.ofMinutes(2));

//        UIBotTestUtils.validateProjectFrameClosed(remoteRobot);
    }

    /**
     * Tests Jakarta Language Server code snippet support in a Java source file
     */
    @Test
    @Video
    public void testAbout() {
        WelcomeFrameFixture welcomePage = remoteRobot.find(WelcomeFrameFixture.class, Duration.ofSeconds(10));
        try {
            String xPath = "//div[@accessiblename='Options Menu' and @class='ActionButton']";
            ComponentFixture actionButton = welcomePage.getActionButton(xPath, "10");
            actionButton.click();
//            String xPathNew = "//div[@class='HeavyWeightWindow']//div[@class='JBScrollPane'] and @accessiblename='About'";
//            String xPathNew = "//div[@accessiblename='About' and @class='HeavyWeightWindow']";
//            ContainerFixture actionButtonNew = welcomePage.getActionButtonNew(xPathNew, "10");
//            actionButtonNew.click();
//            RepeatUtilsKt.waitFor(Duration.ofSeconds(16),
//                    Duration.ofSeconds(1),
//                    "Waiting for menu items containing the " + text + " text",
//                    "Menu items containing the " + text + " text were not found",
//                    () -> !findAll(ComponentFixture.class,
//                            byXpath("//div[@class='ActionMenuItem' and @text='" + text + "']")).isEmpty());

            welcomePage.getActionMenuItem("About");
            TestUtils.sleepAndIgnoreException(3);
            Assertions.fail("Failed");
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        remoteRobot.find(WelcomeFrameFixture.class, Duration.ofMinutes(2));

    }
}

