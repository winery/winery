/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TImplementationArtifacts;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.converter.support.DockerUtils;
import org.eclipse.winery.repository.converter.support.ScriptUtils;
import org.eclipse.winery.repository.converter.support.VirtualMachineUtils;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfContainmentUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfContainmentUtil.class);

    public static NodeTypeImplementationId createNodeTypeImplSelf(NodeTypeId reference, TNodeTypeImplementation childid, IRepository repository, Map<String, Collection<DefinitionsChildId>> artifactTemplateIds) throws IOException {
        List<TDeploymentArtifact> deploymentArtifactTemplatesList = new ArrayList<>();
        List<TImplementationArtifacts> implementationArtifactTemplatesList = new ArrayList<>();

        NodeTypeImplementationId newNodeTypeImplId = new NodeTypeImplementationId(QName.valueOf(childid.getQName().toString() + "-self"));
        NodeTypeImplementationId nodeTypeImplementationId = new NodeTypeImplementationId(QName.valueOf(childid.getQName().toString()));

        // it is an already self node type implementation.
        if (nodeTypeImplementationId.getQName().getLocalPart().endsWith("-self")) {
            return nodeTypeImplementationId;
        }

        if (repository.exists(newNodeTypeImplId)) {
            return newNodeTypeImplId;
        }

        try {
            repository.duplicate(nodeTypeImplementationId, newNodeTypeImplId);
            TNodeTypeImplementation element = repository.getElement(newNodeTypeImplId);
            TNodeType oldNodeTypeElement = repository.getElement(reference);
            element.setNodeType(oldNodeTypeElement.getQName());
            repository.setElement(newNodeTypeImplId, element);
        } catch (IOException e) {
            throw new IOException(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR));
        }

        Collection<DefinitionsChildId> deploymentArtifactIds = artifactTemplateIds.get("DeploymentArtifacts");

        for (DefinitionsChildId artifactTemplateId : deploymentArtifactIds) {
            TArtifactTemplate artifactTemplate = repository.getElement(artifactTemplateId);
            deploymentArtifactTemplatesList.add(new TDeploymentArtifact.Builder(artifactTemplate.getName(), artifactTemplate.getType())
                .setArtifactRef(QName.valueOf("{http://opentosca.org/artifacttemplates}" + artifactTemplate.getId())).build());
        }

        TDeploymentArtifacts deploymentArtifacts = new TDeploymentArtifacts.Builder(deploymentArtifactTemplatesList).build();

        TNodeTypeImplementation nodeTypeImpl = repository.getElement(newNodeTypeImplId);
        nodeTypeImpl.setDeploymentArtifacts(deploymentArtifacts);

        repository.setElement(newNodeTypeImplId, nodeTypeImpl);

        return newNodeTypeImplId;
    }

    public static Collection<DefinitionsChildId> fetchSelfNodeTypeImpls(Collection<DefinitionsChildId> referenceIds, IRepository repository) {
        Collection<DefinitionsChildId> manipulatedList = new ArrayList();
        Collection<DefinitionsChildId> toAdd = new ArrayList();
        for (DefinitionsChildId id : referenceIds) {
            if (id instanceof NodeTypeImplementationId && id.getQName().getLocalPart().endsWith("-self")) {
                toAdd.add(id);
            }
        }

        if (!toAdd.isEmpty()) {
            for (DefinitionsChildId id : referenceIds) {
                if (id instanceof NodeTypeImplementationId && !((ArrayList<DefinitionsChildId>) toAdd).get(0).equals(id)) {
                    continue;
                }
                manipulatedList.add(id);
            }
            return manipulatedList;
        }

        return referenceIds;
    }

    public Map<String, Collection<DefinitionsChildId>> manageSelfContainedDefinitions(Collection<DefinitionsChildId> referencedDefinitionsChildIds, IRepository repository) throws IOException, JAXBException, RepositoryCorruptException {
        Collection<DefinitionsChildId> deploymentArtifactList = new HashSet<>();
        Collection<DefinitionsChildId> implementationArtifactList = new HashSet<>();
        Map<String, Collection<DefinitionsChildId>> artifactTemplates = new HashMap<>();

        for (DefinitionsChildId childid : referencedDefinitionsChildIds) {
            // FIXME: Resolving Ubuntu is based on its name in several places. Any better way to do it?
            if (childid instanceof NodeTypeImplementationId && childid.getQName().toString().startsWith(OpenToscaBaseTypes.ubuntuNodeTypeImpl.toString()) && !childid.getQName().getLocalPart().endsWith("self")) {
                DefinitionsChildId supportedNodeType = isAlreadyExistingNodeTypeImplementation(childid, repository);
                if (supportedNodeType != null) {
                    deploymentArtifactList.add(supportedNodeType);
                    artifactTemplates.put("DeploymentArtifacts", deploymentArtifactList);
                    continue;
                } else {
                    ArtifactTemplateId artifactTemplateForUbuntuNew = VirtualMachineUtils.createSelfArtifactTemplateForUbuntu((NodeTypeImplementationId) childid, repository);
                    deploymentArtifactList.add(artifactTemplateForUbuntuNew);
                    artifactTemplates.put("DeploymentArtifacts", deploymentArtifactList);
                    continue;
                }
            }

            if (childid instanceof ArtifactTemplateId) {
                TArtifactTemplate element = repository.getElement(childid);
                if (element.getType() != null && OpenToscaBaseTypes.dockerContainerArtifactType.equals(element.getType())) {
                    DefinitionsChildId supportedId = doesSelfContainedArtifactTemplateExist(childid, repository);
                    if (supportedId == null) {
                        supportedId = createArtifactTemplate((ArtifactTemplateId) childid, repository, "-self");
                        DockerUtils.buildDockerImage((ArtifactTemplateId) supportedId, repository);
                    }
                    deploymentArtifactList.add(supportedId);
                    artifactTemplates.put("DeploymentArtifacts", deploymentArtifactList);
                } else if ("ScriptArtifact".equals(element.getType().getLocalPart())) {
                    DefinitionsChildId selfContainedArtifactTemplateId = doesSelfContainedArtifactTemplateExist(childid, repository);
                    if (selfContainedArtifactTemplateId != null) {
                        deploymentArtifactList.add(selfContainedArtifactTemplateId);
                        artifactTemplates.put("DeploymentArtifacts", deploymentArtifactList);

                        continue;
                    }

                    ArtifactTemplateId newScriptDeploymentArtifactId = createArtifactTemplate((ArtifactTemplateId) childid, repository, "-self");
                    ArtifactTemplateId updatedScriptArtifactId = createArtifactTemplate((ArtifactTemplateId) childid, repository, "-script-self");

                    String updatedScript = ScriptUtils.resolveScriptArtifact(newScriptDeploymentArtifactId, updatedScriptArtifactId, repository);

                    if (StringUtils.isNotBlank(updatedScript)) {
                        implementationArtifactList.add(updatedScriptArtifactId);
                        artifactTemplates.put("ImplementationArtifacts", implementationArtifactList);

                        deploymentArtifactList.add(newScriptDeploymentArtifactId);
                        artifactTemplates.put("DeploymentArtifacts", deploymentArtifactList);
                    } else {
                        repository.forceDelete(newScriptDeploymentArtifactId);
                        repository.forceDelete(updatedScriptArtifactId);
                    }
                }
            }
        }
        return artifactTemplates;
    }

    private DefinitionsChildId isAlreadyExistingNodeTypeImplementation(DefinitionsChildId childid, IRepository repository) {

        NodeTypeImplementationId nodeTypeImplId = new NodeTypeImplementationId(QName.valueOf(childid.getQName().toString() + "-self"));

        if (repository.exists(nodeTypeImplId)) {
            return nodeTypeImplId;
        } else {
            return null;
        }
    }

    private DefinitionsChildId doesSelfContainedArtifactTemplateExist(DefinitionsChildId childid, IRepository repository) {
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId(QName.valueOf(childid.getQName().toString() + "-self"));
        if (repository.exists(artifactTemplateId)) {
            return artifactTemplateId;
        } else {
            return null;
        }
    }

    private ArtifactTemplateId createArtifactTemplate(ArtifactTemplateId artifactTemplateId, IRepository repository, String suffix) throws IOException {
        String artifactTemplateName = "{http://opentosca.org/artifacttemplates}" + artifactTemplateId.getQName().getLocalPart() + suffix;
        ArtifactTemplateId newArtifactTemplateId = new ArtifactTemplateId(QName.valueOf(artifactTemplateName));

        if (repository.exists(newArtifactTemplateId)) {
            return newArtifactTemplateId;
        }

        try {
            repository.duplicate(artifactTemplateId, newArtifactTemplateId);
            BackendUtils.synchronizeReferences(repository, newArtifactTemplateId);
        } catch (IOException e) {
            throw new IOException(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR));
        }

        return newArtifactTemplateId;
    }
}
