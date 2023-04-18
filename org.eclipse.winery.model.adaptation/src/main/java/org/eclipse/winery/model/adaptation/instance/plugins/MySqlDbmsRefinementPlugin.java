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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.eclipse.winery.model.adaptation.instance.InstanceModelUtils.getClosestVersionMatchOfVersion;

public class MySqlDbmsRefinementPlugin extends InstanceModelRefinementPlugin {

    private static final Logger logger = LoggerFactory.getLogger(MySqlDbmsRefinementPlugin.class);

    private static final String namespace = "{http://opentosca.org/nodetypes}";
    private static final String mySQLName = "MySQL-DBMS";
    private static final String mariaDBName = "MariaDBMS";

    private final Map<QName, TNodeType> nodeTypes;

    public MySqlDbmsRefinementPlugin(Map<QName, TNodeType> nodeTypes) {
        super("MySQL-DBMS");
        this.nodeTypes = nodeTypes;
    }

    @Override
    public Set<String> apply(TTopologyTemplate topology) {
        Set<String> discoveredNodeIds = new HashSet<>();
        try {
            List<String> outputs = InstanceModelUtils.executeCommands(topology, this.matchToBeRefined.nodeIdsToBeReplaced, this.nodeTypes,
                "/usr/bin/mysql --help | grep ' Ver ' | sed -r 's/(.*)Ver (.*)?, for(.*)/\\2/'",
                "netstat -tulpen | grep mysqld | awk '{print $4}' | sed -r 's/.*:([0-9]+)$/\\1/'"
            );

            String mySQL_DBMS_version = outputs.get(0);
            String mySQL_DBMS_port = outputs.get(1);

            if (mySQL_DBMS_version != null && !mySQL_DBMS_version.isBlank() && !mySQL_DBMS_version.toLowerCase().contains("no such file or directory")) {
                topology.getNodeTemplates().stream()
                    .filter(node -> this.matchToBeRefined.nodeIdsToBeReplaced.contains(node.getId())
                        && Objects.requireNonNull(node.getType()).getLocalPart().toLowerCase().contains("DBMS".toLowerCase()))
                    .findFirst()
                    .ifPresent(mySQL -> {
                        WineryVersion wineryVersion = VersionUtils.getVersion(Objects.requireNonNull(mySQL.getType()).getLocalPart());
                        String[] versionSplit = mySQL_DBMS_version.split("\\s");
                        String version = versionSplit[0];
                        logger.info("Found MySQL DBMS version \"{}\"", version);

                        // Case 15.1 Distrib 10.3.38-MariaDB
                        if (versionSplit.length > 1) {
                            String[] split = versionSplit[2].split("-");
                            version = split[0];
                            if (wineryVersion.getComponentVersion() == null || !wineryVersion.getComponentVersion().contains(version)) {
                                mySQL.setType(getClosestVersionMatchOfVersion(namespace, mariaDBName, version, this.nodeTypes));
                            }
                        } else {
                            if (wineryVersion.getComponentVersion() == null || !wineryVersion.getComponentVersion().contains(version)) {
                                mySQL.setType(getClosestVersionMatchOfVersion(namespace, mySQLName, version, this.nodeTypes));
                            }
                        }
                        if (mySQL.getProperties() == null) {
                            mySQL.setProperties(new TEntityTemplate.WineryKVProperties());
                        }
                        if (mySQL.getProperties() instanceof TEntityTemplate.WineryKVProperties properties
                        && mySQL_DBMS_port != null && !mySQL_DBMS_port.isBlank() && !mySQL_DBMS_port.toLowerCase().contains("no such file or directory")) {
                            properties.getKVProperties().put("DBMSPort", mySQL_DBMS_port);
                        }

                        discoveredNodeIds.add(mySQL.getId());
                    });
            }
        } catch (RuntimeException e) {
            logger.error("Error while retrieving Tomcat information...", e);
        }

        return discoveredNodeIds;
    }

    @Override
    public Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced) {
        return InstanceModelUtils.getRequiredInputs(template, nodeIdsToBeReplaced, this.nodeTypes);
    }

    @Override
    protected List<TTopologyTemplate> getDetectorGraphs() {
        IRepository repository = RepositoryFactory.getRepository();

        TNodeType mySQLType = repository.getElement(new NodeTypeId(QName.valueOf(namespace + mySQLName)));
        TNodeTemplate mySQL_DBMS = ModelUtilities.instantiateNodeTemplate(mySQLType);

        return Collections.singletonList(
            new TTopologyTemplate.Builder()
                .addNodeTemplate(mySQL_DBMS)
                .build()
        );
    }
}
