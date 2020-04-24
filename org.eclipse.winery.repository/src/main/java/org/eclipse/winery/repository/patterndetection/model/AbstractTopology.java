/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.patterndetection.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.jgrapht.graph.SimpleDirectedGraph;

public class AbstractTopology {

    private static final String propertiesFilename = "patterndetection.properties";

    private String labelVirtualHardware;
    private String labelServer;
    private String labelService;
    private String labelOS;
    private String labelApp;
    private String labelMessaging;
    private String labelStorage;

    private String relationDeployedOn;
    private String relationHostedOn;
    private String relationDependsOn;
    private String relationConnectsTo;

    private SimpleDirectedGraph<TNodeTemplateExtended, RelationshipEdge> abstractTopology;
    private List<TNodeTemplateExtended> allNodes;

    private Properties properties;

    public AbstractTopology(TTopologyTemplate tTopologyTemplate, List<TNodeTemplateExtended> labeled) {
        properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        labelVirtualHardware = properties.getProperty("labelVirtualHardware");
        labelServer = properties.getProperty("labelServer");
        labelService = properties.getProperty("labelService");
        labelOS = properties.getProperty("labelOS");
        labelApp = properties.getProperty("labelApp");
        labelStorage = properties.getProperty("labelStorage");
        labelMessaging = properties.getProperty("labelMessaging");

        relationDeployedOn = properties.getProperty("relationDeployedOn");
        relationHostedOn = properties.getProperty("relationHostedOn");
        relationDependsOn = properties.getProperty("relationDependsOn");
        relationConnectsTo = properties.getProperty("relationConnectsTo");

        abstractTopology = new SimpleDirectedGraph<>((RelationshipEdge.class));

        // necessary list to avoid duplicate adding of node templates
        allNodes = new ArrayList<>();

        List<TNodeTemplate> tNodeTemplateList = tTopologyTemplate.getNodeTemplates();
        List<TRelationshipTemplate> tRelationshipTemplateList = tTopologyTemplate.getRelationshipTemplates();

        // check for each NodeTemplate if it occurs in the list of labeled NodeTemplates
        all:
        for (TNodeTemplate tNodeTemplate : tNodeTemplateList) {
            if (!labeled.isEmpty()) {
                label:
                for (TNodeTemplateExtended tNodeTemplateExtended : labeled) {
                    // if this node is already labeled, add the correspondent, already created NodeTemplateExtended to the AbstractTopology
                    if (tNodeTemplateExtended.getNodeTemplate().getId().equals(tNodeTemplate.getId()) && !allNodes.contains(tNodeTemplateExtended)) {
                        abstractTopology.addVertex(tNodeTemplateExtended);
                        allNodes.add(tNodeTemplateExtended);
                        continue all;
                    }
                }
                // if it is not already labeled, create a new TNodeTemplateExtended with empty values for keyword + label
                TNodeTemplateExtended temp = new TNodeTemplateExtended(tNodeTemplate, "", "");
                abstractTopology.addVertex(temp);
                allNodes.add(temp);

                // if any node template is labeled
            } else {
                TNodeTemplateExtended temp = new TNodeTemplateExtended(tNodeTemplate, "", "");
                abstractTopology.addVertex(temp);
                allNodes.add(temp);
            }
        }

        // add the according relationship templates
        for (TRelationshipTemplate tRelationshipTemplate : tRelationshipTemplateList) {
            TNodeTemplate target = (TNodeTemplate) tRelationshipTemplate.getTargetElement().getRef();
            TNodeTemplate source = (TNodeTemplate) tRelationshipTemplate.getSourceElement().getRef();
            TNodeTemplateExtended targetNode = new TNodeTemplateExtended();
            TNodeTemplateExtended sourceNode = new TNodeTemplateExtended();
            for (TNodeTemplateExtended node : allNodes) {
                if (node.getNodeTemplate().getId().equals(target.getId())) {
                    targetNode = node;
                } else if (node.getNodeTemplate().getId().equals(source.getId())) {
                    sourceNode = node;
                }
            }
            abstractTopology.addEdge(sourceNode, targetNode, new RelationshipEdge(sourceNode, targetNode, tRelationshipTemplate.getType().getLocalPart()));
        }
    }

    /**
     * Map the whole TOSCA topology with labels, according to Figure 4.9 in the bachelor's thesis
     *
     * @param baseNode: the lowest node with no outgoing relations in the topology
     */
    public void map(TNodeTemplateExtended baseNode) {
        Set<RelationshipEdge> edges = abstractTopology.incomingEdgesOf(baseNode);
        if (edges.isEmpty()) {
            return;
        }
        String label = baseNode.getLabel();
        if (label.equals(labelVirtualHardware)) {
            Iterator iterator = edges.iterator();
            while (iterator.hasNext()) {
                RelationshipEdge edge = (RelationshipEdge) iterator.next();
                TNodeTemplateExtended source = (TNodeTemplateExtended) edge.getV1();
                String edgeLabel = edge.toString();
                if (source.getLabel().isEmpty()) {
                    if (edgeLabel.equals(relationHostedOn)) {
                        source.setLabel(labelOS);
                        map(source);
                    } else if (edgeLabel.equals(relationConnectsTo)) {
                        source.setLabel(labelService);
                        map(source);
                    }
                } else {
                    map(source);
                }
            }
            return;
        } else if (label.equals(labelOS)) {
            Iterator iterator = edges.iterator();
            while (iterator.hasNext()) {
                RelationshipEdge edge = (RelationshipEdge) iterator.next();
                TNodeTemplateExtended source = (TNodeTemplateExtended) edge.getV1();
                String edgeLabel = edge.toString();
                if (source.getLabel().isEmpty()) {
                    if (edgeLabel.equals(relationDeployedOn)) {
                        source.setLabel(labelApp);
                        map(source);
                    } else if (edgeLabel.equals(relationHostedOn)) {
                        //probability for a service is very high because messaging, storage and server components are mostly labeled during keywords
                        source.setLabel(labelService);
                        map(source);
                    }
                } else {
                    map(source);
                }
            }
            return;
        } else if (label.equals(labelService)) {
            Iterator iterator = edges.iterator();
            while (iterator.hasNext()) {
                RelationshipEdge edge = (RelationshipEdge) iterator.next();
                TNodeTemplateExtended source = (TNodeTemplateExtended) edge.getV1();
                String edgeLabel = edge.toString();
                if (source.getLabel().isEmpty()) {
                    if (edgeLabel.equals(relationDependsOn)) {
                        //probability that the unlabeled node is an application is very high, because server would be detected + labeled during keyword search,
                        // probability that service depends on service is very low
                        source.setLabel(labelApp);
                        map(source);
                    }
                } else {
                    map(source);
                }
            }
            return;
        } else if (label.equals(labelServer)) {
            Iterator iterator = edges.iterator();
            while (iterator.hasNext()) {
                RelationshipEdge edge = (RelationshipEdge) iterator.next();
                TNodeTemplateExtended source = (TNodeTemplateExtended) edge.getV1();
                String edgeLabel = edge.toString();
                if (source.getLabel().isEmpty()) {
                    if (edgeLabel.equals(relationHostedOn)) {
                        source.setLabel(labelService);
                        map(source);
                    } else if (edgeLabel.equals(relationDeployedOn)) {
                        source.setLabel(labelApp);
                        map(source);
                    }
                } else {
                    map(source);
                }
            }
            return;
        } else if (label.equals(labelApp)) {
            Iterator iterator = edges.iterator();
            while (iterator.hasNext()) {
                RelationshipEdge edge = (RelationshipEdge) iterator.next();
                TNodeTemplateExtended source = (TNodeTemplateExtended) edge.getV1();
                String edgeLabel = edge.toString();
                if (source.getLabel().isEmpty()) {
                    if (edgeLabel.equals(relationConnectsTo)) {
                        source.setLabel(labelService);
                        map(source);
                    }
                } else {
                    map(source);
                }
            }
            return;
        }
    }

    public SimpleDirectedGraph<TNodeTemplateExtended, RelationshipEdge> getGraph() {
        return abstractTopology;
    }
}
