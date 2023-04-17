/*******************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.instance.plugins.dockerimage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.instance.InstanceModelUtils;
import org.eclipse.winery.model.adaptation.instance.plugins.SpringWebAppRefinementPlugin;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAnalyzerSpring implements DockerLogsAnalyzer {

    private static final Logger logger = LoggerFactory.getLogger(LogAnalyzerSpring.class);
    private final Map<QName, TNodeType> nodeTypes;

    public LogAnalyzerSpring(Map<QName, TNodeType> nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    @Override
    public boolean analyzeLog(String log, TTopologyTemplate topology, List<String> nodeIdsToBeReplaced,
                              String containerNodeId, Set<String> discoveredNodeIds) {
        Pattern pattern = Pattern.compile("(:: Spring Boot ::)(\\s*)\\((v\\d\\.\\d\\.\\d\\..*)\\)");
        Matcher matcher = pattern.matcher(log);

        if (matcher.find()) {
            String springVersion = matcher.group(3);
            logger.info("Found Spring application in Spring version \"{}\"", springVersion);

            TNodeTemplate webApp = InstanceModelUtils.getOrAddNodeTemplateMatchingTypeAndHost(topology, containerNodeId,
                SpringWebAppRefinementPlugin.springWebApp, this.nodeTypes);

            discoveredNodeIds.add(webApp.getId());
            TEntityTemplate.Properties properties = webApp.getProperties();
            if (properties == null) {
                properties = new TEntityTemplate.WineryKVProperties();
                webApp.setProperties(properties);
            }

            if (properties instanceof TEntityTemplate.WineryKVProperties props) {
                props.getKVProperties()
                    .put("springVersion", springVersion);
            }

            return true;
        }

        return false;
    }
}
