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
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

public class SpringWebAppRefinementPlugin extends InstanceModelRefinementPlugin {

    private static final QName springWebApp = QName.valueOf("{http://opentosca.org/nodetypes}SpringWebApp_w1");
    private static final QName petClinic = QName.valueOf("{https://examples.opentosca.org/edmm/nodetypes}Pet_Clinic_w1");

    public SpringWebAppRefinementPlugin() {
        super("SpringWebApplication");
    }

    @Override
    public TTopologyTemplate apply(TTopologyTemplate template) {
        return null;
    }

    @Override
    public Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced) {
        return null;
    }

    @Override
    protected ArrayList<TTopologyTemplate> getDetectorGraphs() {
        IRepository repository = RepositoryFactory.getRepository();

        ArrayList<TTopologyTemplate> detectors = new ArrayList<>();
        detectors.add(createSpringWebAppDetector(repository));
        detectors.add(createPetClinicDetector(repository));

        return detectors;
    }

    private TTopologyTemplate createSpringWebAppDetector(IRepository repository) {
        TNodeType springWebAppType = repository.getElement(new NodeTypeId(springWebApp));
        TNodeTemplate springApp = ModelUtilities.instantiateNodeTemplate(springWebAppType);

        return new TTopologyTemplate.Builder()
            .addNodeTemplate(springApp)
            .build();
    }

    private TTopologyTemplate createPetClinicDetector(IRepository repository) {
        TNodeType petClinicType = repository.getElement(new NodeTypeId(petClinic));
        TNodeTemplate petClinic = ModelUtilities.instantiateNodeTemplate(petClinicType);

        return new TTopologyTemplate.Builder()
            .addNodeTemplate(petClinic)
            .build();
    }
}
