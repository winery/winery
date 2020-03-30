/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.threatmodeling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.repository.common.Util;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TBoolean;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TPolicies;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKVList;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreatModelingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreatModelingUtils.class);
    private static final String SNS_NODE_TYPE = "{http://opentosca.org/nfv/security}S-NS_w1-wip1";

    private final IRepository repository;

    public ThreatModelingUtils(IRepository repo) {
        repository = repo;
    }

    /**
     * Check if Service Template has a suitable substitutable Node Type property.
     *
     * @param serviceTemplate to check
     * @return boolean
     */
    public Boolean correctSubstitutableNodeType(TServiceTemplate serviceTemplate) {

        QName substitutableNodeType = serviceTemplate.getSubstitutableNodeType();

        if (Objects.nonNull(substitutableNodeType)) {
            return isOfTypeOrInheritsFromSVNF(substitutableNodeType);
        }

        return false;
    }

    /**
     * Check if a NodeType is of type "S-VNF.Security" or inherits from a Node Type that is. Searches in the inheritance
     * path for the first occurence of the desired Node Type
     *
     * @param nodeTypeName to check
     * @return boolean
     */
    public Boolean isOfTypeOrInheritsFromSVNF(QName nodeTypeName) {

        NodeTypeId id = new NodeTypeId(nodeTypeName);
        TNodeType nodeType = repository.getElement(id);

        // check for target type 
        if (nodeTypeName.toString().equals(ThreatModelingConstants.SVNF_NODE_TYPE) || nodeTypeName.toString().equals(SNS_NODE_TYPE)) {
            return true;
            // check for target type in inheritance
        } else if (Objects.nonNull(nodeType.getDerivedFrom())) {
            return isOfTypeOrInheritsFromSVNF(nodeType.getDerivedFrom().getTypeAsQName());
        }

        return false;
    }

    /**
     * Find the first abstract Type of a Node Type. Either the Node Type is abstract or its inheritance path is walked
     * recursively to find one.
     *
     * @param nodeTypeName to check
     * @return QName of first abstract Node Type
     * @throws Exception if no abstract Node Type can be found in the inheritance path.
     */
    public QName findFirstAbstractType(QName nodeTypeName) throws Exception {
        NodeTypeId id = new NodeTypeId(nodeTypeName);
        TNodeType nodeType = repository.getElement(id);

        // check if node type is abstract
        if (nodeType.getAbstract().value().equals("yes")) {
            return nodeTypeName;
        } else if (Objects.nonNull(nodeType.getDerivedFrom())) { // check if node type inherits from other node type
            return findFirstAbstractType(nodeType.getDerivedFrom().getTypeAsQName());
        } else {
            throw new Exception("No abstract Node Type found");
        }
    }

    /**
     * Check if a Service Template has a boundary definitions policy of the desired type ("S-VNF.Mitigation)
     *
     * @param serviceTemplate to check
     * @return boolean
     */
    public Boolean hasBoundaryDefinitionMitigationPolicy(TServiceTemplate serviceTemplate) {

        List<TPolicy> mitigationPolicies = getBoundaryDefinitionMitigationPolicies(serviceTemplate);

        return !mitigationPolicies.isEmpty();
    }

    /**
     * Get all policies of a Service Template's boundary definitions that are of type "S-VNF.Mitigation"
     *
     * @param serviceTemplate to obtain policies of
     * @return a list of mitigation policies
     */
    public List<TPolicy> getBoundaryDefinitionMitigationPolicies(TServiceTemplate serviceTemplate) {

        TBoundaryDefinitions boundaryDefinitions = serviceTemplate.getBoundaryDefinitions();
        List<TPolicy> mitigationPolicies = new ArrayList<>();

        // check if boundary definitions are not empty
        if (Objects.nonNull(boundaryDefinitions)) {
            // get all policies
            TPolicies serviceTemplatePolicies = boundaryDefinitions.getPolicies();
            if (Objects.nonNull(serviceTemplatePolicies)) {
                mitigationPolicies = serviceTemplatePolicies.getPolicy()
                    .stream() // check if polcies are mitigation policies
                    .filter((TPolicy policy) -> policy.getPolicyType().toString().equals(ThreatModelingConstants.MITIGATION_POLICY_ID))
                    .collect(Collectors.toList());
            }
        }

        return mitigationPolicies;
    }

    /**
     * Retreive all threat Policy Templates available in the current repository
     *
     * @return a list of all threat policy templates
     */
    public List<Threat> getThreatCatalogue() {

        return this.repository.getAllDefinitionsChildIds(PolicyTemplateId.class)
            .stream()
            .map(repository::getElement)
            .filter((TPolicyTemplate policyTemplate) -> policyTemplate.getTypeAsQName().toString().equals(ThreatModelingConstants.THREAT_POLICY_ID))
            .map((TPolicyTemplate policyTemplate) -> {
                Threat threat = new Threat();
                if (Objects.nonNull(policyTemplate.getProperties())) {
                    threat.setProperties(policyTemplate.getProperties().getKVProperties());
                }
                threat.setTemplateName(policyTemplate.getName());
                threat.setNamespace(policyTemplate.getTypeAsQName().getNamespaceURI());
                return threat;
            })
            .collect(Collectors.toList());
    }

    /**
     * Creates matching threat mitigations policy template pairs.
     *
     * @return status string passed to user
     * @throws IOException when persistence fails
     */
    public String createThreatAndMitigationTemplates(ThreatCreationApiData data) throws IOException {
        
        /* check prerequisites for implicit setup */
        if (!checkPrerequisites()) {
            try {
                setupThreatModelingTypes();
            } catch (Exception e) {
                return "Error while setting up threat modeling types";
            }
        }
        
        /* Create Threat */
        TPolicyTemplate threat = new TPolicyTemplate();
        QName threatTypeQName = QName.valueOf(ThreatModelingConstants.THREAT_POLICY_ID);

        String threatName = Util.makeNCName(data.getName());
        PolicyTemplateId threatID = BackendUtils.getDefinitionsChildId(PolicyTemplateId.class, ThreatModelingConstants.THREATMODELING_NAMESPACE, threatName, false);
        threat.setId(threatName);
        threat.setName(threatName);
        threat.setType(threatTypeQName);

        TPolicyTemplate.Properties threatProps = new TPolicyTemplate.Properties();
        Map<String, String> propMap = new HashMap<>();
        propMap.put(ThreatModelingProperties.description.toString(), data.getDescription());
        propMap.put(ThreatModelingProperties.strideClassification.toString(), data.getStride());
        propMap.put(ThreatModelingProperties.severity.toString(), data.getSeverity());
        threatProps.setKVProperties(propMap);
        threat.setProperties(threatProps);

        Definitions threatDefinitions = BackendUtils.createWrapperDefinitions(threatID, repository);
        threatDefinitions.setElement(threat);

        try {
            BackendUtils.persist(repository, threatID, threatDefinitions);
        } catch (IOException i) {
            LOGGER.debug("Could not save new threat", i);
            return "Could not save new threat";
        }

        TPolicyTemplate mitigation = new TPolicyTemplate();
        QName mitigationTypeQName = QName.valueOf(ThreatModelingConstants.MITIGATION_POLICY_ID);
        String mitigationName = "MITIGATE_".concat(threatName);
        PolicyTemplateId mitigationID = BackendUtils.getDefinitionsChildId(PolicyTemplateId.class, ThreatModelingConstants.THREATMODELING_NAMESPACE, mitigationName, false);

        mitigation.setId(mitigationName);
        mitigation.setName(mitigationName);
        mitigation.setType(mitigationTypeQName);

        TPolicyTemplate.Properties mitigationProps = new TPolicyTemplate.Properties();
        Map<String, String> mitigationPropMap = new HashMap<>();

        mitigationPropMap.put(ThreatModelingProperties.ThreatReference.toString(), threatID.getQName().toString());

        mitigationProps.setKVProperties(mitigationPropMap);
        mitigation.setProperties(mitigationProps);

        Definitions mitigationDefinitions = BackendUtils.createWrapperDefinitions(threatID, repository);
        mitigationDefinitions.setElement(mitigation);

        try {
            BackendUtils.persist(repository, mitigationID, mitigationDefinitions);
        } catch (IOException i) {
            LOGGER.debug("Could not save new threat", i);
            return "Could not save new mitigation";
        }

        return "Threat created!";
    }

    /**
     * Checks the prequisites for threat modeling. These include the base types (S-VNF.Threat and S-VNF.Mitigtion) and
     * an empty SVNF Node Type
     *
     * @return boolean
     */
    public boolean checkPrerequisites() {
        PolicyTypeId threatId = new PolicyTypeId(QName.valueOf(ThreatModelingConstants.THREAT_POLICY_ID));
        PolicyTypeId mitigationId = new PolicyTypeId(QName.valueOf(ThreatModelingConstants.MITIGATION_POLICY_ID));
        NodeTypeId svnfId = new NodeTypeId(QName.valueOf(ThreatModelingConstants.SVNF_NODE_TYPE));

        return repository.exists(threatId) && repository.exists(mitigationId) && repository.exists(svnfId);
    }

    /**
     * create all Policy Types and Node Types required for threat modeling
     *
     * @throws Exception if setup was already done
     */
    public void setupThreatModelingTypes() throws Exception {

        if (checkPrerequisites()) {
            throw new Exception("Threat modeling already set up.");
        }
        TPolicyType threat = new TPolicyType();
        threat.setId(ThreatModelingConstants.THREAT_POLICY_NAME);
        threat.setName(ThreatModelingConstants.THREAT_POLICY_NAME);
        threat.setAbstract(TBoolean.NO);
        threat.setFinal(TBoolean.NO);

        threat.setTargetNamespace(ThreatModelingConstants.THREATMODELING_NAMESPACE);

        threat.setProperties(null);

        WinerysPropertiesDefinition threatProps = new WinerysPropertiesDefinition();
        PropertyDefinitionKVList threatPropList = new PropertyDefinitionKVList();
        threatProps.setElementName("properties");
        threatProps.setNamespace(ThreatModelingConstants.THREATMODELING_NAMESPACE.concat("/propertiesdefinition/winery"));

        threatPropList.add(new PropertyDefinitionKV(ThreatModelingProperties.description.toString(), "xsd:string"));
        threatPropList.add(new PropertyDefinitionKV(ThreatModelingProperties.strideClassification.toString(), "xsd:string"));
        threatPropList.add(new PropertyDefinitionKV(ThreatModelingProperties.severity.toString(), "xsd:string"));
        threatProps.setPropertyDefinitionKVList(threatPropList);

        ModelUtilities.replaceWinerysPropertiesDefinition(threat, threatProps);

        PolicyTypeId threatID = BackendUtils.getDefinitionsChildId(PolicyTypeId.class, ThreatModelingConstants.THREATMODELING_NAMESPACE, ThreatModelingConstants.THREAT_POLICY_NAME, false);
        Definitions threatDefinitions = BackendUtils.createWrapperDefinitions(threatID, repository);

        threatDefinitions.setElement(threat);

        TPolicyType mitigation = new TPolicyType();

        mitigation.setId(ThreatModelingConstants.MITIGATION_POLICY_NAME);
        mitigation.setName(ThreatModelingConstants.MITIGATION_POLICY_NAME);
        mitigation.setAbstract(TBoolean.NO);
        mitigation.setFinal(TBoolean.NO);

        mitigation.setTargetNamespace(ThreatModelingConstants.THREATMODELING_NAMESPACE);

        mitigation.setProperties(null);

        WinerysPropertiesDefinition mitigationProps = new WinerysPropertiesDefinition();
        PropertyDefinitionKVList mitigationPropList = new PropertyDefinitionKVList();
        mitigationProps.setElementName("properties");
        mitigationProps.setNamespace(ThreatModelingConstants.THREATMODELING_NAMESPACE.concat("/propertiesdefinition/winery"));

        mitigationPropList.add(new PropertyDefinitionKV(ThreatModelingProperties.ThreatReference.toString(), "xsd:string"));
        mitigationProps.setPropertyDefinitionKVList(mitigationPropList);

        ModelUtilities.replaceWinerysPropertiesDefinition(mitigation, mitigationProps);

        PolicyTypeId mitigationID = BackendUtils.getDefinitionsChildId(PolicyTypeId.class, ThreatModelingConstants.THREATMODELING_NAMESPACE, ThreatModelingConstants.MITIGATION_POLICY_NAME, false);
        Definitions mitigationDefinitions = BackendUtils.createWrapperDefinitions(mitigationID, repository);

        mitigationDefinitions.setElement(mitigation);

        TNodeType svnf = new TNodeType.Builder("S-VNF-w1_wip1")
            .setTargetNamespace(ThreatModelingConstants.SECURITY_NAMESPACE)
            .setAbstract(TBoolean.YES)
            .build();

        NodeTypeId svnfID = new NodeTypeId(QName.valueOf(ThreatModelingConstants.SVNF_NODE_TYPE));
        Definitions svnfDefinitions = BackendUtils.createWrapperDefinitions(svnfID, repository);
        svnfDefinitions.setElement(svnf);

        try {
            BackendUtils.persist(repository, threatID, threatDefinitions);
            BackendUtils.persist(repository, mitigationID, mitigationDefinitions);
            BackendUtils.persist(repository, svnfID, svnfDefinitions);
        } catch (IOException i) {
            LOGGER.debug("Could not set up threat modeling", i);
        }
    }
}
