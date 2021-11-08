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
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlDbmsRefinementPlugin extends InstanceModelRefinementPlugin {

    private static final Logger logger = LoggerFactory.getLogger(MySqlDbmsRefinementPlugin.class);

    private static final QName mySqlName = QName.valueOf("{http://opentosca.org/nodetypes}MySQL-DBMS");
    private static final QName mySql_5_5_QName = QName.valueOf("{http://opentosca.org/nodetypes}MySQL-DBMS_5.5-w1");
    private static final QName mySql_5_7_QName = QName.valueOf("{http://opentosca.org/nodetypes}MySQL-DBMS_5.7-w1");

    public MySqlDbmsRefinementPlugin() {
        super("MySQL-DBMS");
    }

    @Override
    public TTopologyTemplate apply(TTopologyTemplate template) {
        try {
            Session session = InstanceModelUtils.createJschSession(template, this.matchToBeRefined.nodeIdsToBeReplaced);
            String mySQL_DBMS_version = InstanceModelUtils.executeCommand(
                session,
                "sudo /usr/bin/mysql --help | grep Distrib | awk '{print $5}' | sed -r 's/([0-9]+),$/\\1/'"
            );
            String mySQL_DBMS_port = InstanceModelUtils.executeCommand(
                session,
                "sudo netstat -tulpen | grep mysqld | awk '{print $4}' | sed -r 's/.*:([0-9]+)$/\\1/'"
            );

            session.disconnect();

            template.getNodeTemplates().stream()
                .filter(node -> this.matchToBeRefined.nodeIdsToBeReplaced.contains(node.getId())
                    && Objects.requireNonNull(node.getType()).getLocalPart().toLowerCase().startsWith("MySQL-DBMS".toLowerCase()))
                .findFirst()
                .ifPresent(mySQL -> {
                    WineryVersion version = VersionUtils.getVersion(Objects.requireNonNull(mySQL.getType()).getLocalPart());
                    String[] split = mySQL_DBMS_version.split("\\.");

                    if (version.getComponentVersion() == null || !version.getComponentVersion().startsWith(split[0])) {
                        if ("5".equals(split[0]) && "5".equals(split[1])) {
                            mySQL.setType(mySql_5_5_QName);
                        } else if ("5".equals(split[0]) && "7".equals(split[1])) {
                            mySQL.setType(mySql_5_7_QName);
                        }
                    }
                    if (mySQL.getProperties() == null) {
                        mySQL.setProperties(new TEntityTemplate.WineryKVProperties());
                    }
                    if (mySQL.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                        TEntityTemplate.WineryKVProperties properties = (TEntityTemplate.WineryKVProperties) mySQL.getProperties();
                        properties.getKVProperties().put("DBMSPort", mySQL_DBMS_port);
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

        TNodeType mySQLType = repository.getElement(new NodeTypeId(mySqlName));
        TNodeTemplate mySQL_DBMS = ModelUtilities.instantiateNodeTemplate(mySQLType);

        return Collections.singletonList(
            new TTopologyTemplate.Builder()
                .addNodeTemplate(mySQL_DBMS)
                .build()
        );
    }
}
