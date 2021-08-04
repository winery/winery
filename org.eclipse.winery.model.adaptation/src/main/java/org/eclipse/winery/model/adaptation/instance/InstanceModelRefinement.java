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

package org.eclipse.winery.model.adaptation.instance;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.winery.model.adaptation.instance.plugins.Ec2AmiRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.MySqlDbRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.MySqlDbmsRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.PetClinicRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.SpringWebAppRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.TomcatRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.plugins.dockerimage.DockerImageRefinementPlugin;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.topologygraph.model.ToscaGraph;
import org.eclipse.winery.topologygraph.transformation.ToscaTransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceModelRefinement {

    Logger logger = LoggerFactory.getLogger(InstanceModelRefinement.class);

    private final InstanceModelPluginChooser pluginChooser;
    private final List<InstanceModelRefinementPlugin> plugins;

    public InstanceModelRefinement(InstanceModelPluginChooser chooser) {
        this.pluginChooser = chooser;
        this.plugins = Arrays.asList(
            new TomcatRefinementPlugin(),
            new MySqlDbRefinementPlugin(),
            new MySqlDbmsRefinementPlugin(),
            new PetClinicRefinementPlugin(),
            new SpringWebAppRefinementPlugin(),
            new Ec2AmiRefinementPlugin(),
            new DockerImageRefinementPlugin()
        );
    }

    public TTopologyTemplate refine(ServiceTemplateId serviceTemplateId) {
        IRepository repository = RepositoryFactory.getRepository();
        TServiceTemplate serviceTemplate = repository.getElement(serviceTemplateId);
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();

        if (topologyTemplate == null) {
            logger.error("Cannot refine empty instance model!");
            return null;
        }

        boolean pluginsAreAvailable = true;
        do {
            ToscaGraph topologyGraph = ToscaTransformer.createTOSCAGraph(topologyTemplate);
            List<InstanceModelRefinementPlugin> executablePlugins = this.plugins.stream()
                .filter(plugin -> plugin.isApplicable(topologyTemplate, topologyGraph))
                .collect(Collectors.toList());
            InstanceModelRefinementPlugin selectedPlugin = pluginChooser.selectPlugin(topologyTemplate,
                executablePlugins);

            if (selectedPlugin != null) {
                selectedPlugin.apply(topologyTemplate);
                try {
                    repository.setElement(serviceTemplateId, serviceTemplate);
                } catch (IOException e) {
                    logger.error("Error persisting Service Template {}", serviceTemplateId.toReadableString());
                }
            } else {
                pluginsAreAvailable = false;
            }
        } while (pluginsAreAvailable);

        return topologyTemplate;
    }
}
