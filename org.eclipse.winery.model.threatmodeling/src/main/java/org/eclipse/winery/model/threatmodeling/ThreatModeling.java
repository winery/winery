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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TPolicy;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreatModeling {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreatModeling.class);

    private final IRepository repository;
    private final ThreatModelingUtils utils;
    private List<TNodeTemplate> nodeTemplates;
    private TServiceTemplate serviceTemplate;
    private Map<QName, Threat> abstractThreatMitigations = new HashMap<>();

    public ThreatModeling(final ServiceTemplateId serviceTemplateId) {

        LOGGER.info("Threatmodeling");

        repository = RepositoryFactory.getRepository();
        this.utils = new ThreatModelingUtils(repository);
        serviceTemplate = repository.getElement(serviceTemplateId);
        nodeTemplates = serviceTemplate.getTopologyTemplate().getNodeTemplates();
    }

    // overloaded constructor to supply a custom repository (mainly used for testing)
    ThreatModeling(final ServiceTemplateId serviceTemplateId, IRepository repository) {

        LOGGER.info("Threatmodeling");
        this.repository = repository;
        this.utils = new ThreatModelingUtils(repository);
        serviceTemplate = repository.getElement(serviceTemplateId);
        nodeTemplates = serviceTemplate.getTopologyTemplate().getNodeTemplates();
    }

    /**
     * Search for Service Templates that can be used as substitutes for abstract SVNF groups
     *
     * @return a list of Service Templates
     */
    private List<TServiceTemplate> getServiceTemplateSubstitutionCandidates() {
        return this.repository.getAllDefinitionsChildIds(ServiceTemplateId.class)
            .stream()
            .map(repository::getElement)
            // check if ServiceTemplate is substituable for one of the target NodeTypes:
            .filter(utils::correctSubstitutableNodeType)
            // check if ServiceTemplate has policy in boundaryDefinitions to be an adequate mitigation for a given threat
            .filter(utils::hasBoundaryDefinitionMitigationPolicy)
            .collect(Collectors.toList());
    }

    /**
     * find abstract SVNF group Node Types for appropriate mitigations based on the present threats of the current
     * Service Template
     */
    private void findAbstractMitigationsForPresentThreats() {

        List<TServiceTemplate> mitigationCandidates = getServiceTemplateSubstitutionCandidates();

        mitigationCandidates.forEach((TServiceTemplate template) -> {

            QName substitutableNodeType = template.getSubstitutableNodeType();
            QName abstractMitigationType;

            try {
                abstractMitigationType = utils.findFirstAbstractType(substitutableNodeType);
            } catch (Exception e) {
                return;
            }

            List<TPolicy> mitigationPolicies = utils.getBoundaryDefinitionMitigationPolicies(template);
            mitigationPolicies.forEach((TPolicy mitigation) -> {
                PolicyTemplateId mitigationTemplateId = new PolicyTemplateId(mitigation.getPolicyRef());
                TPolicyTemplate mitigationTemplate = repository.getElement(mitigationTemplateId);

                if (Objects.nonNull(mitigationTemplate.getProperties())) {
                    LinkedHashMap<String, String> properties = ModelUtilities.getPropertiesKV(mitigationTemplate);
                    // FIXME Assumption that we're dealing with simple KV Properties
                    String threatReferenceString = (String)properties.get("ThreatReference");

                    QName threatReference = QName.valueOf(threatReferenceString);
                    // check if the threat, the mitigation references is present.
                    if (Objects.nonNull(abstractThreatMitigations.get(threatReference))) {
                        // add abstract SVNF to list of effective mitigations to present threat

                        Threat threat = abstractThreatMitigations.get(threatReference);
                        threat.addMitigations(abstractMitigationType);
                        abstractThreatMitigations.put(threatReference, threat);
                    }
                }
            });
        });
    }

    /**
     * Collect all threats present in the current Service Template
     */
    private void getPresentThreats() {
        nodeTemplates.forEach((TNodeTemplate nt) -> {
            if (nt.getPolicies() != null) {
                nt.getPolicies().getPolicy().forEach((TPolicy policy) -> {
                    String policyTypeName = policy.getPolicyType().toString();
                    if (policyTypeName.equals(ThreatModelingConstants.THREAT_POLICY_ID)) {

                        QName threatQName = policy.getPolicyRef();
                        PolicyTemplateId threatId = new PolicyTemplateId(threatQName);
                        TPolicyTemplate threatTemplate = repository.getElement(threatId);

                        Threat threat = new Threat();
                        // set target of current threat.
                        threat.addTarget(nt.getName(), nt.getTypeAsQName());

                        if (Objects.nonNull(threatTemplate.getProperties())) {
                            threat.setProperties(ModelUtilities.getPropertiesKV(threatTemplate));
                        }
                        threat.setTemplateName(threatQName.getLocalPart());
                        threat.setNamespace(threatQName.getNamespaceURI());

                        abstractThreatMitigations.putIfAbsent(threatQName, threat);
                    }
                });
            }
        });
    }

    /**
     * Collect all SVNFs that are already present in the topology template of the current Service Template
     *
     * @return a list of stringified QNames of NodeTemplates
     */
    private List<String> getPresentSVNFs() {
        return nodeTemplates.stream()
            .map(TEntityTemplate::getType)
            .filter(utils::isOfTypeOrInheritsFromSVNF)
            .map(QName::toString)
            .collect(Collectors.toList());
    }

    /**
     * Assess threats and SVNFs of current Service Template
     *
     * @return threat assessment
     */
    public ThreatAssessment getServiceTemplateThreats() {

        ThreatAssessment threatAssessment = new ThreatAssessment();

        getPresentThreats();
        findAbstractMitigationsForPresentThreats();

        threatAssessment.setThreats(this.abstractThreatMitigations);
        threatAssessment.setSVNFs(this.getPresentSVNFs());

        return threatAssessment;
    }
}
