/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.tools.intellij.it.fixtures;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.CommonContainerFixture;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.DefaultXpath;
import com.intellij.remoterobot.fixtures.FixtureName;
import com.intellij.remoterobot.search.locators.Locator;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.utils.UtilsKt.hasAnyComponent;

/**
 * Welcome page view fixture.
 */
@DefaultXpath(by = "FlatWelcomeFrame type", xpath = "//div[@class='FlatWelcomeFrame']")
@FixtureName(name = "Welcome Frame")
public class WelcomeFrameFixture extends CommonContainerFixture {

    public WelcomeFrameFixture(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
        super(remoteRobot, remoteComponent);
    }

    public ComponentFixture getOpenProjectComponentFixture(String text) {
        ComponentFixture cf;

        // The Welcome page may show two different views.
        if (hasAnyComponent(this, byXpath("//div[@class='JBOptionButton' and @text='Open'] "))) {
            // Handle the view that shows the list of recently used projects
            cf = find(ComponentFixture.class, byXpath("//div[@class='JBOptionButton' and @text='Open']"));
        } else {
            // Handle the view that does not show any recently used projects.
            cf = find(ComponentFixture.class, byXpath("//div[@class='JButton' and @defaulticon='open.svg']"));
        }
        return cf;
    }

    public ComponentFixture getActionButton(String... vars) {
        String xPath = vars[0];
        int waitTime = Integer.parseInt(vars[1]);

        Locator locator = byXpath(xPath);
        return find(ComponentFixture.class, locator, Duration.ofSeconds(waitTime));
    }
}
