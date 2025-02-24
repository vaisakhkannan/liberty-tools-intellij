/*******************************************************************************
* Copyright (c) 2019, 2023 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package io.openliberty.tools.intellij.lsp4jakarta.it.core;

import com.intellij.maven.testFramework.MavenImportingTestCase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.IdeaTestUtil;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.builders.ModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.*;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Modified from:
 * https://github.com/eclipse/lsp4mp/blob/bc926f75df2ca103d78c67b997c87adb7ab480b1/microprofile.jdt/org.eclipse.lsp4mp.jdt.test/src/main/java/org/eclipse/lsp4mp/jdt/core/BasePropertiesManagerTest.java
 * With certain methods modified or deleted to fit the purposes of LSP4Jakarta
 *
 */
public abstract class BaseJakartaTest extends MavenImportingTestCase {

    protected TestFixtureBuilder<IdeaProjectTestFixture> myProjectBuilder;

    @Override
    protected void setUpFixtures() throws Exception {
        // Don't call super.setUpFixtures() here, that will create FocusListener leak.
        myProjectBuilder = IdeaTestFixtureFactory.getFixtureFactory().createFixtureBuilder(getName());
        final JavaTestFixtureFactory factory = JavaTestFixtureFactory.getFixtureFactory();
        ModuleFixtureBuilder moduleBuilder = myProjectBuilder.addModule(JavaModuleFixtureBuilder.class);
        final var testFixture = factory.createCodeInsightFixture(myProjectBuilder.getFixture());
        setTestFixture(testFixture);
        testFixture.setUp();
        LanguageLevelProjectExtension.getInstance(testFixture.getProject()).setLanguageLevel(LanguageLevel.JDK_15);
        Sdk value = ProjectRootManager.getInstance(testFixture.getProject()).getProjectSdk();
        Sdk value1 = ModuleRootManager.getInstance(testFixture.getModule()).getSdk();
        System.out.println("-------- "+ value);
        System.out.println("-------- "+ value1);
    }

    private static AtomicInteger counter = new AtomicInteger(0);

    /**
     * Create a new module into the test project from existing project folder.
     *
     * @param projectDirs the project folders
     * @return the created modules
     */
    protected List<Module> createMavenModules(List<File> projectDirs) throws Exception {
        Project project = getTestFixture().getProject();
        List<VirtualFile> pomFiles = new ArrayList<>();
        for (File projectDir : projectDirs) {
            File moduleDir = new File(project.getBasePath(), projectDir.getName() + counter.getAndIncrement());
            FileUtils.copyDirectory(projectDir, moduleDir);
            VirtualFile pomFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(moduleDir).findFileByRelativePath("pom.xml");
            pomFiles.add(pomFile);

        }
        Sdk value = ProjectRootManager.getInstance(getTestFixture().getProject()).getProjectSdk();
        Sdk value1 = ModuleRootManager.getInstance(getTestFixture().getModule()).getSdk();
        Sdk sdk = ProjectJdkTable.getInstance().getAllJdks()[0]; // Get the first available JDK
//        ApplicationManager.getApplication().runWriteAction(() -> {
//            ProjectRootManager.getInstance(getTestFixture().getProject()).setProjectSdk(sdk);
//        });

        Sdk value2 = ProjectRootManager.getInstance(getTestFixture().getProject()).getProjectSdk();

        System.out.println("-------- "+ value);
        System.out.println("-------- "+ value1);
        System.out.println("-------- "+ value2);

        System.out.println("-------- "+ sdk);

        // do this once
//        Sdk jdk11 = IdeaTestUtil.getMockJdk11();

        // do this for every module
//        ModuleRootModificationUtil.setModuleSdk(module, jdk11);


        importProjects(pomFiles.toArray(VirtualFile[]::new));
        Module[] modules = ModuleManager.getInstance(getTestFixture().getProject()).getModules();
        Sdk jdk11 =IdeaTestUtil.getMockJdk11();
        Sdk[] values = JavaAwareProjectJdkTableImpl.getInstanceEx().getAllJdks();
        Sdk va = JavaAwareProjectJdkTableImpl.getInstanceEx().getInternalJdk();
//        String vaNew = "/Users/vaisakht/.gradle/caches/transforms-4/b65e30a80a1aafe3dbc3a997cb22772f/transformed/ideaIC-2024.1-aarch64/jbr/Contents/Home)";

//        System.out.println("-------- "+ vaa);

        System.out.println("-------- "+ values);

        System.out.println("-------- "+ va);
        System.out.println("-------- "+ jdk11);
        WriteAction.runAndWait(() -> ProjectJdkTable.getInstance()
                .addJdk(va, getTestRootDisposable()));
        for (Module module : modules) {
//            setupJdkForModule(module.getName());
            // do this for every module
            ModuleRootModificationUtil.setModuleSdk(module, va);
        }

        Sdk valueNew = ProjectRootManager.getInstance(getTestFixture().getProject()).getProjectSdk();
        Sdk valueNew1 = ModuleRootManager.getInstance(getTestFixture().getModule()).getSdk();

        System.out.println("-------- "+ valueNew);
        System.out.println("-------- "+ valueNew1);

        // REVISIT: After calling setupJdkForModule() initialization appears to continue in the background
        // and a may cause a test to intermittently fail if it accesses the module too early. A 10-second wait
        // is hopefully long enough but would be preferable to synchronize on a completion event if one is
        // ever introduced in the future.
        Thread.sleep(10000L);
        // QuarkusProjectService.getInstance(myTestFixture.getProject()).processModules();
        return Arrays.asList(modules).stream().skip(1).collect(Collectors.toList());
    }

    protected Module createMavenModule(File projectDir) throws Exception {
        List<Module> modules = createMavenModules(Collections.singletonList(projectDir));
        Sdk valueNew = ProjectRootManager.getInstance(getTestFixture().getProject()).getProjectSdk();
        Sdk valueNew1 = ModuleRootManager.getInstance(getTestFixture().getModule()).getSdk();
        return modules.get(modules.size() - 1);
    }

}
