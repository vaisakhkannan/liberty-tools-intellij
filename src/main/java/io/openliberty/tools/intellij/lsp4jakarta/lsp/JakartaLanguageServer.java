/*******************************************************************************
 * Copyright (c) 2020, 2025 Red Hat, Inc. and others.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 * IBM Corporation
 ******************************************************************************/
package io.openliberty.tools.intellij.lsp4jakarta.lsp;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.vfs.VirtualFile;
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider;
import io.openliberty.tools.intellij.util.Constants;
import io.openliberty.tools.intellij.util.JavaVersionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JakartaLanguageServer extends OSProcessStreamConnectionProvider {
    private static final String JAR_DIR = "lib/server/";
    private static final String LANGUAGESERVER_JAR = "org.eclipse.lsp4jakarta.ls-jar-with-dependencies.jar";
    private static final Logger LOGGER = LoggerFactory.getLogger(JakartaLanguageServer.class);

    public JakartaLanguageServer() {
        String javaHome = System.getProperty("java.home");
        IdeaPluginDescriptor descriptor = PluginManagerCore.getPlugin(PluginId.getId("open-liberty.intellij"));
        File lsp4JakartaServerPath = new File(descriptor.getPluginPath().toFile(), JAR_DIR + LANGUAGESERVER_JAR);
        if(!JavaVersionUtil.isJavaHomeValid(javaHome, Constants.JAKARTA_LANG_SERVER)){
            return;
        }
        if (lsp4JakartaServerPath.exists()) {
            setCommandLine(new GeneralCommandLine(Arrays.asList(javaHome + File.separator + "bin" + File.separator + "java", "-jar",
                    lsp4JakartaServerPath.getAbsolutePath(), "-DrunAsync=true")));
        } else {
            LOGGER.warn(String.format("Unable to start Eclipse LSP4Jakarta. Eclipse LSP4Jakarta server path: %s does not exist"), lsp4JakartaServerPath);
        }
    }

    @Override
    public Object getInitializationOptions(VirtualFile rootUri) {
        Map<String, Object> root = new HashMap<>();
        Map<String, Object> extendedClientCapabilities = new HashMap<>();
        extendedClientCapabilities.put("shouldLanguageServerExitOnShutdown", Boolean.TRUE);
        root.put("extendedClientCapabilities", extendedClientCapabilities);
        return root;
    }
}
