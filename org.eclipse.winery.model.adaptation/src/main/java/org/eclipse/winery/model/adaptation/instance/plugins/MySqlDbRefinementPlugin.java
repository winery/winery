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
import java.util.Set;

import javax.xml.namespace.QName;

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

public class MySqlDbRefinementPlugin extends InstanceModelRefinementPlugin {

    public static final QName mySqlDbQName = QName.valueOf("{http://opentosca.org/nodetypes}MySQL-DB");

    private static final Logger logger = LoggerFactory.getLogger(MySqlDbRefinementPlugin.class);

    public MySqlDbRefinementPlugin() {
        super("MySQL-DB");
    }

    @Override
    public TTopologyTemplate apply(TTopologyTemplate template) {
        Session session = InstanceModelUtils.createJschSession(template, this.matchToBeRefined.nodeIdsToBeReplaced);
        String mySqlDatabases = InstanceModelUtils.executeCommand(
            session,
            "sudo mysql -sN -e \"SELECT schema_name from INFORMATION_SCHEMA.SCHEMATA  WHERE schema_name NOT IN('information_schema', 'mysql', 'performance_schema'\n" +
                ", 'sys');\""
        );
        logger.info("Found MySqlDatabases: {}", mySqlDatabases);

        session.disconnect();

        if (!mySqlDatabases.isEmpty()) {
            String[] identifiedDBs = mySqlDatabases.split("\\n");

            template.getNodeTemplates().stream()
                .filter(node -> this.matchToBeRefined.nodeIdsToBeReplaced.contains(node.getId())
                    && node.getType().getLocalPart().toLowerCase().startsWith(mySqlDbQName.getLocalPart().toLowerCase()))
                .findFirst()
                .ifPresent(db -> {
                    if (db.getProperties() instanceof TEntityTemplate.WineryKVProperties) {
                        TEntityTemplate.WineryKVProperties properties = (TEntityTemplate.WineryKVProperties) db.getProperties();
                        properties.getKVProperties().put("DBName", identifiedDBs[0]);
                    }
                });
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

        TNodeType mySQLType = repository.getElement(new NodeTypeId(mySqlDbQName));
        TNodeTemplate mySQL_DBMS = ModelUtilities.instantiateNodeTemplate(mySQLType);

        return Collections.singletonList(
            new TTopologyTemplate.Builder()
                .addNodeTemplate(mySQL_DBMS)
                .build()
        );
    }
}
