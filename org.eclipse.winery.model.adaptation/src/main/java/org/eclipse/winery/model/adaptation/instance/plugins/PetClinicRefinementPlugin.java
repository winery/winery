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
import java.util.LinkedHashMap;
import java.util.List;
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

import com.jcraft.jsch.Session;

import static org.eclipse.winery.model.adaptation.instance.plugins.MySqlDbRefinementPlugin.mySqlDbQName;

public class PetClinicRefinementPlugin extends InstanceModelRefinementPlugin {

    public static final QName petClinic = QName.valueOf("{https://examples.opentosca.org/edmm/nodetypes}Pet_Clinic_w1");

    public PetClinicRefinementPlugin() {
        super("PetClinic");
    }

    @Override
    public TTopologyTemplate apply(TTopologyTemplate topology) {
        Optional<TNodeTemplate> first = topology.getNodeTemplates().stream()
            .filter(node -> this.matchToBeRefined.nodeIdsToBeReplaced.contains(node.getId())
                && node.getType().equals(petClinic))
            .findFirst();

        if (first.isPresent()) {
            TNodeTemplate petClinicNode = first.get();
            if (petClinicNode.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                LinkedHashMap<String, String> kvProperties = ((TEntityTemplate.WineryKVProperties) petClinicNode.getProperties()).getKVProperties();

                Session session = InstanceModelUtils.createJschSession(topology, this.matchToBeRefined.nodeIdsToBeReplaced);
                String databaseType = InstanceModelUtils.executeCommand(
                    session,
                    "sudo find /opt/tomcat/latest/webapps/" + kvProperties.get("context").trim()
                        + " -name application-mysql.properties -exec cat {} \\; "
                        + "| grep database= | sed -r 's/database=(.*)$/\\1/'"
                );

                if (databaseType.trim().equals("mysql")) {
                    String dbName = InstanceModelUtils.executeCommand(
                        session,
                        "sudo cat /opt/tomcat/latest/webapps/" + kvProperties.get("context").trim()
                            + "/WEB-INF/classes/db/mysql/schema.sql | grep USE | sed -r 's/USE (.*);$/\\1/'"
                    );
                    String dbUser = InstanceModelUtils.executeCommand(
                        session,
                        " sudo cat /opt/tomcat/latest/webapps/petclinic-pet_clinic/WEB-INF/classes/db/mysql/schema.sql"
                            + " | grep 'IDENTIFIED BY' | sed -r 's/(.*)IDENTIFIED BY (.*);$/\\2/'"
                    );

                    topology.getNodeTemplates().stream()
                        .filter(node -> node.getType().equals(mySqlDbQName))
                        .filter(node -> node.getProperties() != null
                            && node.getProperties() instanceof TEntityTemplate.WineryKVProperties
                            && ((TEntityTemplate.WineryKVProperties) node.getProperties()).getKVProperties().get("DBName") != null
                            && ((TEntityTemplate.WineryKVProperties) node.getProperties()).getKVProperties().get("DBName").equals(dbName)
                        )
                        .findFirst()
                        .ifPresent(db -> {
                            ((TEntityTemplate.WineryKVProperties) db.getProperties()).getKVProperties()
                                .put("DBUser", dbUser.replaceAll("(')|(\")", ""));
                            ModelUtilities.createRelationshipTemplateAndAddToTopology(
                                petClinicNode, db, ToscaBaseTypes.connectsToRelationshipType, topology
                            );
                        });
                }

                session.disconnect();
            }
        }

        return topology;
    }

    @Override
    public Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced) {
        Set<String> inputs = InstanceModelUtils.getRequiredSSHInputs(template, nodeIdsToBeReplaced);
        return inputs.isEmpty() ? null : inputs;
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
