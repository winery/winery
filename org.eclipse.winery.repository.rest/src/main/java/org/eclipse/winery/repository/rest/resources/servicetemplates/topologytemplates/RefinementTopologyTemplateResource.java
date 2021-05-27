/*******************************************************************************
 * Copyright (c) 2020-2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.servicetemplates.topologytemplates;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.extensions.OTAttributeMapping;
import org.eclipse.winery.model.tosca.extensions.OTBehaviorPatternMapping;
import org.eclipse.winery.model.tosca.extensions.OTDeploymentArtifactMapping;
import org.eclipse.winery.model.tosca.extensions.OTPatternRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTPermutationMapping;
import org.eclipse.winery.model.tosca.extensions.OTPrmMapping;
import org.eclipse.winery.model.tosca.extensions.OTRefinementModel;
import org.eclipse.winery.model.tosca.extensions.OTRelationMapping;
import org.eclipse.winery.model.tosca.extensions.OTStayMapping;
import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResourceContainingATopology;

public class RefinementTopologyTemplateResource extends TopologyTemplateResource {
    private static TTopologyTemplate prmModellingTopologyTemplate = new TTopologyTemplate();

    /**
     * A topology template is always nested in a service template
     */
    public RefinementTopologyTemplateResource(AbstractComponentInstanceResourceContainingATopology parent, OTRefinementModel refinementModel, String type) {
        super(parent, prmModellingTopologyTemplate, type);
        createPrmTopologyTemplate(refinementModel);
    }

    /**
     * merge NodeTemplates from Detector and refinement Structure into one Topolologytemplate
     */
    private void createPrmTopologyTemplate(OTRefinementModel refinementModel) {
        final int setPointDetectorAvg = 370;
        final int setPointRefinementAvg = 1330;
        int dAvg = nodesPositionAverage(refinementModel.getDetector().getNodeTemplates());
        int rAvg = nodesPositionAverage(refinementModel.getRefinementTopology().getNodeTemplates());
        prmModellingTopologyTemplate.setNodeTemplates(new ArrayList());
        prmModellingTopologyTemplate.setRelationshipTemplates(new ArrayList());

        for (TNodeTemplate nodeTemplate : refinementModel.getDetector().getNodeTemplates()) {
            TNodeTemplate newNodeTemplate = nodeTemplate;
            int oldX = Integer.parseInt(newNodeTemplate.getX());
            int newX = setPointDetectorAvg + (oldX - dAvg) / 2;
            newNodeTemplate.setX(String.valueOf(newX));
            prmModellingTopologyTemplate.addNodeTemplate(newNodeTemplate);
        }
        for (TNodeTemplate nodeTemplate : refinementModel.getRefinementTopology().getNodeTemplates()) {
            TNodeTemplate newNodeTemplate = nodeTemplate;
            int oldX = Integer.parseInt(newNodeTemplate.getX());
            int newX = setPointRefinementAvg + (oldX - rAvg) / 2;
            newNodeTemplate.setX(String.valueOf(newX));
            prmModellingTopologyTemplate.addNodeTemplate(newNodeTemplate);
        }
        for (TRelationshipTemplate relationshipTemplate : refinementModel.getDetector().getRelationshipTemplates()) {
            prmModellingTopologyTemplate.addRelationshipTemplate(relationshipTemplate);
        }
        for (TRelationshipTemplate relationshipTemplate : refinementModel.getRefinementTopology().getRelationshipTemplates()) {
            prmModellingTopologyTemplate.addRelationshipTemplate(relationshipTemplate);
        }
        createRelationshipsForMappings(refinementModel);
    }

    private int nodesPositionAverage(List<TNodeTemplate> nodeTemplates) {
        int sum = 0;
        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            sum += Integer.parseInt(nodeTemplate.getX());
        }
        if (nodeTemplates.size() > 0) {
            return sum / nodeTemplates.size();
        } else {
            return 0;
        }
    }

    /**
     * convert mappings into Relationship for use in grafic prm modeling
     */
    private void createRelationshipsForMappings(OTRefinementModel refinementModel) {
        for (OTPrmMapping mapping : getAllMappings(refinementModel)) {
            TRelationshipTemplate.SourceOrTargetElement sourceElement = new TRelationshipTemplate.SourceOrTargetElement();
            sourceElement.setRef(new TNodeTemplate(mapping.getDetectorElement().getId()));
            TRelationshipTemplate.SourceOrTargetElement targetElement = new TRelationshipTemplate.SourceOrTargetElement();
            targetElement.setRef(new TNodeTemplate(mapping.getRefinementElement().getId()));
            TRelationshipTemplate.Builder builder = new TRelationshipTemplate.Builder("con_" + mapping.getId(), QName.valueOf("{http://opentosca.org/prmMappingTypes}" + mapping.getId().substring(0, mapping.getId().indexOf("_"))), sourceElement, targetElement);
            if (mapping instanceof OTPermutationMapping) {
                builder.setName("PermutationMapping");
            }
            if (mapping instanceof OTRelationMapping) {
                builder.setName("RelationshipMapping");
                LinkedHashMap<String, String> kvproperties = new LinkedHashMap<>();
                kvproperties.put("direction", ((OTRelationMapping) mapping).getDirection().value());
                kvproperties.put("applicableRelationshipType", ((OTRelationMapping) mapping).getRelationType().toString());
                kvproperties.put("validEndpointType", ((OTRelationMapping) mapping).getValidSourceOrTarget().toString());
                TEntityTemplate.WineryKVProperties properties = new TEntityTemplate.WineryKVProperties();
                properties.setKVProperties(kvproperties);
                builder.setProperties(properties);
            }
            if (mapping instanceof OTStayMapping) {
                builder.setName("StayMapping");
            }
            if (mapping instanceof OTAttributeMapping) {
                builder.setName("AttributeMapping");
                LinkedHashMap<String, String> kvproperties = new LinkedHashMap<>();
                kvproperties.put("type", ((OTAttributeMapping) mapping).getType().value());
                kvproperties.put("detectorProperty", ((OTAttributeMapping) mapping).getDetectorProperty());
                kvproperties.put("refinementProperty", ((OTAttributeMapping) mapping).getRefinementProperty());
                TEntityTemplate.WineryKVProperties properties = new TEntityTemplate.WineryKVProperties();
                properties.setKVProperties(kvproperties);
                builder.setProperties(properties);
            }
            if (mapping instanceof OTDeploymentArtifactMapping) {
                builder.setName("DeploymentArtifactMapping");
                LinkedHashMap<String, String> kvproperties = new LinkedHashMap<>();
                kvproperties.put("requiredDeploymentArtifactType", ((OTDeploymentArtifactMapping) mapping).getArtifactType().toString());
                TEntityTemplate.WineryKVProperties properties = new TEntityTemplate.WineryKVProperties();
                properties.setKVProperties(kvproperties);
                builder.setProperties(properties);
            }
            if (mapping instanceof OTBehaviorPatternMapping) {
                builder.setName("BehaviorPatternMapping");
                LinkedHashMap<String, String> kvproperties = new LinkedHashMap<>();
                kvproperties.put("refinementProperty", ((OTBehaviorPatternMapping) mapping).getProperty().getKey());
                kvproperties.put("refinementPropertyValue", ((OTBehaviorPatternMapping) mapping).getProperty().getValue());
                kvproperties.put("behaviorPattern", ((OTBehaviorPatternMapping) mapping).getBehaviorPattern());
                TEntityTemplate.WineryKVProperties properties = new TEntityTemplate.WineryKVProperties();
                properties.setKVProperties(kvproperties);
                builder.setProperties(properties);
            }
            TRelationshipTemplate templateForMapping = new TRelationshipTemplate(builder);
            prmModellingTopologyTemplate.addRelationshipTemplate(templateForMapping);
        }
    }

    private List<OTPrmMapping> getAllMappings(OTRefinementModel refinementModel) {
        List<OTPrmMapping> allPrmMappings = new ArrayList<>();
        allPrmMappings.addAll(refinementModel.getRelationMappings());
        if (refinementModel instanceof OTTopologyFragmentRefinementModel) {
            OTTopologyFragmentRefinementModel model = (OTTopologyFragmentRefinementModel) refinementModel;
            allPrmMappings.addAll(model.getAttributeMappings());
            allPrmMappings.addAll(model.getStayMappings());
            allPrmMappings.addAll(model.getDeploymentArtifactMappings());
            allPrmMappings.addAll(model.getPermutationMappings());
            if (model instanceof OTPatternRefinementModel) {
                allPrmMappings.addAll(((OTPatternRefinementModel) model).getBehaviorPatternMappings());
            }
        }
        return allPrmMappings;
    }
}
