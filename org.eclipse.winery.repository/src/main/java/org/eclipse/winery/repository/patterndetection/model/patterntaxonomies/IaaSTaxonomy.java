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

package org.eclipse.winery.repository.patterndetection.model.patterntaxonomies;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class IaaSTaxonomy {

    private static final String propertiesFilename = "patterndetection.properties";

    private Properties properties;

    private String iaas;
    private String elasticInfrastructure;
    private String publicCloud;
    private String envBasedAv;
    private String nodeBasedAv;
    private String elasticityManager;
    private String elasticLoadBalancer;
    private String elasticQueue;


    private SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> iaasTaxonomie;

    public IaaSTaxonomy() {
        iaasTaxonomie = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        properties = new Properties();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        iaas = properties.getProperty("nodeIaaS");
        elasticInfrastructure = properties.getProperty("nodeElasticInfrastructure");
        envBasedAv = properties.getProperty("nodeEnvBasedAv");
        nodeBasedAv = properties.getProperty("nodeNodeBasedAv");
        publicCloud = properties.getProperty("nodePublicCloud");
        elasticityManager = properties.getProperty("nodeElasticityManager");
        elasticLoadBalancer = properties.getProperty("nodeElasticLoadBalancer");
        elasticQueue = properties.getProperty("nodeElasticQueue");

        iaasTaxonomie.addVertex(iaas);
        iaasTaxonomie.addVertex(elasticInfrastructure);
        iaasTaxonomie.addVertex(envBasedAv);
        iaasTaxonomie.addVertex(nodeBasedAv);
        iaasTaxonomie.addVertex(publicCloud);
        iaasTaxonomie.addVertex(elasticityManager);
        iaasTaxonomie.addVertex(elasticLoadBalancer);
        iaasTaxonomie.addVertex(elasticQueue);

        iaasTaxonomie.addEdge(iaas, elasticInfrastructure);
        iaasTaxonomie.addEdge(elasticInfrastructure, envBasedAv);
        iaasTaxonomie.addEdge(elasticInfrastructure, nodeBasedAv);
        iaasTaxonomie.addEdge(elasticInfrastructure, elasticityManager);
        iaasTaxonomie.addEdge(elasticInfrastructure, elasticLoadBalancer);
        iaasTaxonomie.addEdge(elasticInfrastructure, elasticQueue);
        iaasTaxonomie.addEdge(envBasedAv, publicCloud);
        iaasTaxonomie.addEdge(iaas, nodeBasedAv);
        iaasTaxonomie.addEdge(iaas, envBasedAv);
    }

    public SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> getIaasTaxonomie() {
        return iaasTaxonomie;
    }

    public String getIaas() {
        return iaas;
    }

    public String getElasticInfrastructure() {
        return elasticInfrastructure;
    }

    public String getEnvBasedAv() {
        return envBasedAv;
    }

    public String getNodeBasedAv() {
        return nodeBasedAv;
    }

    public String getPublicCloud() {
        return publicCloud;
    }

    public String getElasticityManager() {
        return elasticityManager;
    }

    public String getElasticLoadBalancer() {
        return elasticLoadBalancer;
    }

    public String getElasticQueue() {
        return elasticQueue;
    }

}
