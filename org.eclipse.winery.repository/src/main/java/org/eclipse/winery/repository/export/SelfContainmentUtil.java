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

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.dependencyresolver.Docker.Docker;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.exceptions.RepositoryCorruptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfContainmentUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfContainmentUtil.class);

    public Collection<DefinitionsChildId> manageSelfContainedDefinitions(Collection<DefinitionsChildId> referencedDefinitionsChildIds, IRepository repository) throws IOException, JAXBException, RepositoryCorruptException {
        Collection<DefinitionsChildId> processedIds = new HashSet<>();

        for (DefinitionsChildId childid : referencedDefinitionsChildIds) {
            // FIXME: Resolving Ubuntu is based on its name in several places. Any better way to do it?
            if (childid instanceof NodeTypeId && childid.getQName().getLocalPart().startsWith("Ubuntu")) {
                DefinitionsChildId supportedNodeType = isAlreadyExistingNodeType(childid, repository);
                if (supportedNodeType != null) {
                    processedIds.add(supportedNodeType);
                    break;
                } else {
                    NodeTypeId selfContainedNodeType = createSelfContainedNodeType((NodeTypeId) childid, repository);
                    Collection<DefinitionsChildId> referencedDefs = repository.getReferencedDefinitionsChildIds(childid);
                    for (DefinitionsChildId nodeTypeImp : referencedDefs) {
                        if (nodeTypeImp.getQName().getLocalPart().contains("Ubuntu-VM")) {
                            prepareSelfContainedNodeTypeImp(selfContainedNodeType, (NodeTypeImplementationId) nodeTypeImp, repository);
                        }
                    }
                    processedIds.add(selfContainedNodeType);
                    break;
                }
            }

            if (childid instanceof ArtifactTemplateId) {
                TArtifactTemplate element = repository.getElement(childid);
                if ("DockerContainerArtifact".equals(element.getType().getLocalPart())) {
                    DefinitionsChildId supportedId = isAlreadyExistingDockerContainer(childid, repository);
                    if (supportedId != null) {
                        processedIds.add(supportedId);
                        continue;
                    } else {
                        Docker docker = new Docker();
                        ArtifactTemplateId artifactTemplateId = duplicateTemplateForDocker((ArtifactTemplateId) childid);
                        TArtifactTemplate dupTemplate = repository.getElement(artifactTemplateId);
                        String fileName = dupTemplate.getArtifactReferences().getArtifactReference().get(0).getReference();
                        fileName = "/Users/emreb/repos/tosca-definitions-internal/" + fileName;
                        docker.proceed(fileName, "");
                        processedIds.add(artifactTemplateId);
                        continue;
                    }
                }
            }
            processedIds.add(childid);
        }
        return processedIds;
    }

    private DefinitionsChildId isAlreadyExistingNodeType(DefinitionsChildId childid, IRepository repository) {

        NodeTypeId nodeTypeImplId = new NodeTypeId(QName.valueOf(childid.getQName().toString() + "-self"));

        if (repository.exists(nodeTypeImplId)) {
            return nodeTypeImplId;
        } else {
            return null;
        }
    }

    private DefinitionsChildId isAlreadyExistingDockerContainer(DefinitionsChildId childid, IRepository repository) {
        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId(QName.valueOf(childid.getQName().toString() + "-self"));
        if (repository.exists(artifactTemplateId)) {
            return artifactTemplateId;
        } else {
            return null;
        }
    }

    private NodeTypeImplementationId prepareSelfContainedNodeTypeImp(NodeTypeId reference, NodeTypeImplementationId childid, IRepository repository) throws IOException {
        List<TDeploymentArtifact> artifactTemplateList = new ArrayList<>();

        NodeTypeImplementationId newNodeTypeImplId = new NodeTypeImplementationId(QName.valueOf(childid.getQName().toString() + "-self"));
        NodeTypeImplementationId nodeTypeImplementationId = new NodeTypeImplementationId(QName.valueOf(childid.getQName().toString()));

        try {
            repository.duplicate(nodeTypeImplementationId, newNodeTypeImplId);
            TNodeTypeImplementation element = repository.getElement(newNodeTypeImplId);
            TNodeType oldNodeTypeElement = repository.getElement(reference);
            element.setNodeType(oldNodeTypeElement.getQName());
            repository.setElement(newNodeTypeImplId, element);
        } catch (IOException e) {
            throw new IOException(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR));
        }

        ArtifactTemplateId artifactTemplateId = createArtifactTemplateForUbuntu(childid);

        TArtifactTemplate artifactTemplate = repository.getElement(artifactTemplateId);
        artifactTemplateList.add(new TDeploymentArtifact.Builder(artifactTemplate.getName(), artifactTemplate.getType())
            .setArtifactRef(QName.valueOf(artifactTemplate.getName())).build());

        TDeploymentArtifacts deploymentArtifacts = new TDeploymentArtifacts.Builder(artifactTemplateList).build();

        TNodeTypeImplementation nodeTypeImpl = RepositoryFactory.getRepository(Paths.get("/Users/emreb/repos/tosca-definitions-internal")).getElement(newNodeTypeImplId);
        nodeTypeImpl.setDeploymentArtifacts(deploymentArtifacts);

        repository.setElement(newNodeTypeImplId, nodeTypeImpl);

        return newNodeTypeImplId;
    }

    private ArtifactTemplateId createArtifactTemplateForUbuntu(NodeTypeImplementationId childid) throws IOException {
        String repositoryPath = "/Users/emreb/repos/tosca-definitions-internal";

        IRepository fileBaseRepository = RepositoryFactory.getRepository(Paths.get("/Users/emreb/repos/tosca-definitions-internal"));

        TArtifactTemplate artifactTemplate = new TArtifactTemplate();

        String artifactTemplateName = "{http://opentosca.org/artifacttemplates}" + childid.getQName().getLocalPart() + "-dependency";
        // TODO: pre-introduced ISO type 
        String artifactTemplateType = "{http://opentosca.org/artifacttypes}ISO_w1-wip1";

        ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId(QName.valueOf(artifactTemplateName));

        artifactTemplate.setName(artifactTemplateName);
        artifactTemplate.setId(artifactTemplateName);
        artifactTemplate.setType(artifactTemplateType);

        fileBaseRepository.setElement(artifactTemplateId, artifactTemplate);

        try {
            String[] splitFileName = childid.getQName().getLocalPart().split("-");
            downloadIsoFile(repositoryPath + "/artifacttemplates/http%3A%2F%2Fopentosca.org%2Fartifacttemplates/" + childid.getQName().getLocalPart() + "-dependency", splitFileName[2]);

            // Set references
            TArtifactReference artifactReference = new TArtifactReference();
            artifactReference.setReference("artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/" + childid.getQName().getLocalPart() + "-dependency" + "/files/" + "dependency.iso");

            TArtifactTemplate.ArtifactReferences artifactReferences = new TArtifactTemplate.ArtifactReferences();
            artifactReferences.getArtifactReference().add(artifactReference);
            artifactTemplate.setArtifactReferences(artifactReferences);

            fileBaseRepository.setElement(artifactTemplateId, artifactTemplate);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return artifactTemplateId;
    }

    private void downloadIsoFile(String path, String version) throws IOException {
        Files.createDirectories(Paths.get(path + "/files"));
        path = path + "/files/dependency.iso";
        URL website = new URL("http://old-releases.ubuntu.com//releases/" + version + ".0/ubuntu-" + version + "-server-amd64.iso");
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(path);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    private ArtifactTemplateId duplicateTemplateForDocker(ArtifactTemplateId artifactTemplateId) throws IOException {
        String artifactTemplateName = "{http://opentosca.org/artifacttemplates}" + artifactTemplateId.getQName().getLocalPart() + "-self";

        ArtifactTemplateId newArtifactTemplateId = new ArtifactTemplateId(QName.valueOf(artifactTemplateName));
        // FIXME: FIND A BETTER WAY TO GET FILE BASED REPOSITORY! THIS IS JUST FOR LOCAL TESTING. FIX IN SEVERAL PLACES
        IRepository repository = RepositoryFactory.getRepository(Paths.get("/Users/emreb/repos/tosca-definitions-internal"));

        try {
            repository.duplicate(artifactTemplateId, newArtifactTemplateId);
        } catch (IOException e) {
            throw new IOException(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR));
        }

        return newArtifactTemplateId;
    }

    private NodeTypeId createSelfContainedNodeType(NodeTypeId id, IRepository repository) throws IOException {
        NodeTypeId newNodeTypelId = new NodeTypeId(QName.valueOf(id.getQName().toString() + "-self"));
        NodeTypeId nodeTypeId = new NodeTypeId(QName.valueOf(id.getQName().toString()));

        try {
            repository.duplicate(nodeTypeId, newNodeTypelId);
        } catch (IOException e) {
            throw new IOException(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR));
        }

        return newNodeTypelId;
    }
}
