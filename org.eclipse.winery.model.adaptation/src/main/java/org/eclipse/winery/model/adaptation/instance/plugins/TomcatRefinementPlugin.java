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

package org.eclipse.winery.model.adaptation.instance.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.InstanceModelUtils;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.ToscaDiscoveryPlugin;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatRefinementPlugin extends InstanceModelRefinementPlugin {

    private static final Logger logger = LoggerFactory.getLogger(TomcatRefinementPlugin.class);

    private static final QName webserver = ToscaBaseTypes.webserver;
    private static final QName tomcatQName = QName.valueOf("{http://opentosca.org/nodetypes}Tomcat");
    private static final QName tomcat7QName = QName.valueOf("{http://opentosca.org/nodetypes}Tomcat_7-w1");
    private static final QName tomcat8QName = QName.valueOf("{http://opentosca.org/nodetypes}Tomcat_8-w1");
    private static final QName tomcat9QName = QName.valueOf("{http://opentosca.org/nodetypes}Tomcat_9-w1");

    public TomcatRefinementPlugin() {
        super("Tomcat");
    }

    @Override
    public TTopologyTemplate apply(
            TTopologyTemplate template,
            ToscaDiscoveryPlugin discoveryPlugin) {
        try {
            Session session = InstanceModelUtils.createJschSession(template, this.matchToBeRefined.nodeIdsToBeReplaced);
            String tomcatVersion = InstanceModelUtils.executeCommand(
                session,
                "sudo cat /opt/tomcat/latest/RELEASE-NOTES | grep 'Apache Tomcat Version' | awk '{print $4}'"
            );
            logger.info("Retrieved Tomcat version: {}", tomcatVersion);

            String tomcatPort = InstanceModelUtils.executeCommand(
                session,
                "sudo cat /opt/tomcat/latest/conf/server.xml | grep '<Connector port=\".*\" protocol=\"HTTP/1.1\"' | awk '{print $2}' | sed -r 's/.*\"([0-9]+)\"$/\\1/'"
            );
            logger.info("Retrieved Tomcat port: {}", tomcatPort);

            session.disconnect();

            template.getNodeTemplates().stream()
                .filter(node -> this.matchToBeRefined.nodeIdsToBeReplaced.contains(node.getId())
                    && Objects.requireNonNull(node.getType()).getLocalPart().toLowerCase().startsWith("Tomcat".toLowerCase()))
                .findFirst()
                .ifPresent(tomcat -> {
                    WineryVersion version = VersionUtils.getVersion(Objects.requireNonNull(tomcat.getType()).getLocalPart());
                    String[] split = tomcatVersion.split("\\.");

                    if (version.getComponentVersion() == null || !version.getComponentVersion().startsWith(split[0])) {
                        if ("7".equals(split[0])) {
                            tomcat.setType(tomcat7QName);
                        } else if ("8".equals(split[0])) {
                            tomcat.setType(tomcat8QName);
                        } else if ("9".equals(split[0])) {
                            tomcat.setType(tomcat9QName);
                        }
                    }
                    if (tomcat.getProperties() == null) {
                        tomcat.setProperties(new TEntityTemplate.WineryKVProperties());
                    }
                    if (tomcat.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                        TEntityTemplate.WineryKVProperties properties = (TEntityTemplate.WineryKVProperties) tomcat.getProperties();
                        properties.getKVProperties().put("Port", tomcatPort);
                    }
                });
        } catch (RuntimeException e) {
            logger.error("Error while retrieving Tomcat information...", e);
        }

        return template;
    }

    @Override
    public Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced) {
        Set<String> inputs = InstanceModelUtils.getRequiredSSHInputs(template, nodeIdsToBeReplaced);
        return inputs.isEmpty() ? null : inputs;
    }

    @Override
    protected List<TTopologyTemplate> getDetectorGraphs() {
        IRepository repository = RepositoryFactory.getRepository();

        TNodeType tomcatType = repository.getElement(new NodeTypeId(tomcatQName));
        TNodeTemplate tomcat = ModelUtilities.instantiateNodeTemplate(tomcatType);

        return Collections.singletonList(
            new TTopologyTemplate.Builder()
                .addNodeTemplate(tomcat)
                .build()
        );
    }
}
