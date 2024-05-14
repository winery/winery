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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;
import org.eclipse.winery.model.adaptation.instance.InstanceModelUtils;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import static org.eclipse.winery.model.adaptation.instance.plugins.MySqlDbRefinementPlugin.mySqlDbQName;

public class PetClinicRefinementPlugin extends InstanceModelRefinementPlugin {

    public static final QName petClinic = QName.valueOf("{https://examples.opentosca.org/edmm/nodetypes}Pet_Clinic_w1");

    public PetClinicRefinementPlugin(Map<QName, TNodeType> nodeTypes) {
        super("PetClinic", nodeTypes);
    }

    @Override
    public Set<String> apply(TTopologyTemplate topology) {
        Set<String> discoveredNodeIds = new HashSet<>();
        Optional<TNodeTemplate> first = topology.getNodeTemplates().stream()
            .filter(node -> this.matchToBeRefined.nodeIdsToBeReplaced.contains(node.getId())
                && Objects.requireNonNull(node.getType()).equals(petClinic))
            .findFirst();

        if (first.isPresent()) {
            TNodeTemplate petClinicNode = first.get();
            if (petClinicNode.getProperties() == null) {
                petClinicNode.setProperties(new TEntityTemplate.WineryKVProperties());
            }

            if (petClinicNode.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                LinkedHashMap<String, String> kvProperties = ((TEntityTemplate.WineryKVProperties) petClinicNode.getProperties()).getKVProperties();

                List<String> outputs = InstanceModelUtils.executeCommands(topology, this.matchToBeRefined.nodeIdsToBeReplaced, this.nodeTypes,
                    "sudo find /opt/tomcat/latest/webapps/" + kvProperties.get("context").trim()
                        + " -name application-mysql.properties -exec cat {} \\; "
                        + "| grep database= | sed -r 's/database=(.*)$/\\1/'"
                );
                String databaseType = outputs.get(0);

                if (databaseType.trim().equals("mysql")) {
                    outputs = InstanceModelUtils.executeCommands(topology, this.matchToBeRefined.nodeIdsToBeReplaced, this.nodeTypes,
                        "sudo cat /opt/tomcat/latest/webapps/" + kvProperties.get("context").trim() + "/WEB-INF/classes/db/mysql/schema.sql | grep USE | sed -r 's/USE (.*);$/\\1/'",
                        "sudo cat /opt/tomcat/latest/webapps/" + kvProperties.get("context").trim() + "/WEB-INF/classes/db/mysql/schema.sql | grep 'IDENTIFIED BY' | sed -r 's/(.*)IDENTIFIED BY (.*);$/\\2/'"
                    );

                    String dbName = outputs.get(0);
                    String dbUser = outputs.get(1);

                    topology.getNodeTemplates().stream()
                        .filter(node -> Objects.requireNonNull(node.getType()).equals(mySqlDbQName))
                        .filter(node -> node.getProperties() != null
                            && node.getProperties() instanceof TEntityTemplate.WineryKVProperties
                            && ((TEntityTemplate.WineryKVProperties) node.getProperties()).getKVProperties().get("DBName") != null
                            && ((TEntityTemplate.WineryKVProperties) node.getProperties()).getKVProperties().get("DBName").equals(dbName)
                        )
                        .findFirst()
                        .ifPresent(db -> {
                            if (db.getProperties() == null) {
                                db.setProperties(new TEntityTemplate.WineryKVProperties());
                            }
                            ((TEntityTemplate.WineryKVProperties) db.getProperties()).getKVProperties()
                                .put("DBUser", dbUser.replaceAll("(')|(\")", ""));
                            ModelUtilities.createRelationshipTemplateAndAddToTopology(
                                petClinicNode, db, ToscaBaseTypes.connectsToRelationshipType, topology
                            );
                            discoveredNodeIds.add(petClinicNode.getId());
                            discoveredNodeIds.add(db.getId());
                        });
                }
            }
        }

        return discoveredNodeIds;
    }

    @Override
    protected List<TTopologyTemplate> getDetectorGraphs() {
        IRepository repository = RepositoryFactory.getRepository();
        TNodeType petClinicType = repository.getElement(new NodeTypeId(petClinic));
        TNodeTemplate petClinic = ModelUtilities.instantiateNodeTemplate(petClinicType);

        if (petClinic.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
            TEntityTemplate.WineryKVProperties properties = (TEntityTemplate.WineryKVProperties) petClinic.getProperties();
            properties.getKVProperties().put("context", "*");
        }

        return Collections.singletonList(
            new TTopologyTemplate.Builder()
                .addNodeTemplate(petClinic)
                .build()
        );
    }
}
