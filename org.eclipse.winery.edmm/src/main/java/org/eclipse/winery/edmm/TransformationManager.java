/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.edmm;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.eclipse.winery.edmm.plugins.PluginManager;
import org.eclipse.winery.edmm.utils.ZipUtility;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.plugin.PluginService;
import io.github.edmm.core.transformation.TransformationContext;
import io.github.edmm.core.transformation.TransformationService;
import io.github.edmm.model.DeploymentModel;

public class TransformationManager {

    public File transform(EntityGraph entityGraph, String target, String wineryRepository) throws Exception {
        PluginService pluginService = PluginManager.getInstance()
            .getPluginService();
        TransformationService transformationService = new TransformationService(pluginService);

        // getting the model from the graph
        DeploymentModel deploymentModel = new DeploymentModel(UUID.randomUUID().toString(), entityGraph);

        // the paths of the artifacts or operation files start from the root directory
        File sourceDirectory = Paths.get(wineryRepository).toFile();
        File targetDirectory = Files.createTempDirectory(target + "-").toFile();

        TransformationContext transformationContext = transformationService.createContext(deploymentModel, target, sourceDirectory, targetDirectory);
        transformationService.start(transformationContext);
        // throws an exception if the transformation wasn't successful
        transformationContext.throwExceptionIfErrorState();

        Path zipPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve(target + ".zip");
        ZipUtility.pack(targetDirectory.toPath(), zipPath);

        return zipPath.toFile();
    }
}
