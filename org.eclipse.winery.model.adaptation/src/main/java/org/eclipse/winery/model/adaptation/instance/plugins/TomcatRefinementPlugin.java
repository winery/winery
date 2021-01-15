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
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.adaptation.instance.InstanceModelRefinementPlugin;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.model.tosca.constants.ToscaBaseTypes;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class TomcatRefinementPlugin extends InstanceModelRefinementPlugin {

    private static final QName webserver = ToscaBaseTypes.webserver;
    private static final QName tomcatQName = QName.valueOf("{http://opentosca.org/nodetypes}Tomcat");
    private static final QName tomcat7QName = QName.valueOf("{http://opentosca.org/nodetypes}Tomcat_7-w1");
    private static final QName tomcat8QName = QName.valueOf("{http://opentosca.org/nodetypes}Tomcat_8-w1");
    private static final QName tomcat9QName = QName.valueOf("{http://opentosca.org/nodetypes}Tomcat_9-w1");

    public TomcatRefinementPlugin() {
        super("Tomcat");
    }

    @Override
    public TTopologyTemplate apply(TTopologyTemplate template) {
        // matcher ohne version
        // get version und tausche aus, wenn nötig
        // getPort --> sed auf server.xml --> conector ODER/UND netstat
        return null;
    }

    @Override
    public Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced) {
        return null;
    }

    @Override
    protected TTopologyTemplate getDetectorGraph() {
        IRepository repository = RepositoryFactory.getRepository();

        TNodeType tomcat7Type = repository.getElement(new NodeTypeId(tomcat7QName));
        TNodeTemplate tomcat7 = ModelUtilities.instantiateNodeTemplate(tomcat7Type);
        TNodeType ubuntuType = repository.getElement(new NodeTypeId(OpenToscaBaseTypes.Ubuntu18NodeType));
        TNodeTemplate ubuntu = ModelUtilities.instantiateNodeTemplate(ubuntuType);

        return new TTopologyTemplate.Builder()
            .addNodeTemplate(tomcat7)
            .addNodeTemplate(ubuntu)
            .addRelationshipTemplate(
                ModelUtilities.createRelationshipTemplate(tomcat7, ubuntu, ToscaBaseTypes.hostedOnRelationshipType)
            )
            .build();
    }
}
