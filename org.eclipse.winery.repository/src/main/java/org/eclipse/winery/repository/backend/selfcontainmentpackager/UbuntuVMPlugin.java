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
package org.eclipse.winery.repository.backend.selfcontainmentpackager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TDeploymentArtifacts;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.repository.datatypes.ids.elements.ArtifactTemplateFilesDirectoryId;

import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UbuntuVMPlugin implements SelfContainmentPlugin {

    private final static Logger logger = LoggerFactory.getLogger(UbuntuVMPlugin.class);
    private final static String imageFileType = ".img";
    private final static String imageDiskType = "-disk1" + imageFileType;

    private final Map<QName, TArtifactTemplate> artifactTemplates;

    private final Map<String, List<String>> codeNamesToVersion = initCodeNames();

    public UbuntuVMPlugin() {
        IRepository repository = RepositoryFactory.getRepository();
        this.artifactTemplates = repository.getQNameToElementMapping(ArtifactTemplateId.class);
    }

    @Override
    public boolean canHandleNodeType(QName nodeType, IRepository repository) {
        return nodeType != null && (
            nodeType.toString().startsWith(OpenToscaBaseTypes.ubuntuNodeType.toString())
        );
    }

    @Override
    public boolean canHandleArtifactType(QName artifactType, IRepository repository) {
        return false;
    }

    @Override
    public void downloadDependenciesBasedOnNodeType(TNodeTypeImplementation nodeTypeImplementation, IRepository repository) {
        QName nodeType = nodeTypeImplementation.getNodeType();

        WineryVersion nodeTypeVersion = VersionUtils.getVersion(nodeType.getLocalPart());
        String componentVersion = nodeTypeVersion.getComponentVersion();
        if (componentVersion != null) {
            String codeName = getCodeName(componentVersion);

            if (codeName != null) {
                logger.info("Found code name '{}' for Ubuntu Node Type {}", codeName, nodeType);

                String nameWithoutVersion = VersionUtils.getNameWithoutVersion(nodeType.getLocalPart());
                WineryVersion artifactVersion = new WineryVersion(nodeTypeVersion.getComponentVersion() + "-CloudImage", 1, 1);
                ArtifactTemplateId artifactTemplateId = new ArtifactTemplateId(
                    OpenToscaBaseTypes.artifactTemplateNamespace,
                    nameWithoutVersion + "-DA" + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + artifactVersion.toString(),
                    false);
                TArtifactTemplate element = repository.getElement(artifactTemplateId);
                element.setType(OpenToscaBaseTypes.cloudImageArtifactType);
                logger.info("Generated ArtifactTemplate {}", artifactTemplateId.getQName());

                if (!repository.exists(artifactTemplateId)) {
                    logger.info("Trying to donwload iamge file...");
                    String baseUrl = "https://cloud-images.ubuntu.com/" +
                        codeName +
                        "/current/" +
                        codeName +
                        "-server-cloudimg-amd64";

                    try {
                        URL url = new URL(baseUrl + imageFileType);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("HEAD");
                        int responseCode = connection.getResponseCode();

                        if (responseCode != 200) {
                            connection.disconnect();
                            logger.info("Image not found, trying with '-disk' suffix...");

                            url = new URL(baseUrl + imageDiskType);
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("HEAD");
                            responseCode = connection.getResponseCode();
                        }

                        if (responseCode == 200) {
                            repository.setElement(artifactTemplateId, element);
                            ArtifactTemplateFilesDirectoryId filesId = new ArtifactTemplateFilesDirectoryId(artifactTemplateId);

                            String fileName = url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
                            RepositoryFileReference repositoryFileReference = new RepositoryFileReference(filesId, fileName);

                            try (InputStream inputStream = url.openStream()) {
                                repository.putContentToFile(repositoryFileReference, inputStream,
                                    MediaType.parse("application/x-image"));
                            }

                            BackendUtils.synchronizeReferences(repository, artifactTemplateId);

                            TDeploymentArtifact imageDa = new TDeploymentArtifact();
                            imageDa.setArtifactType(OpenToscaBaseTypes.cloudImageArtifactType);
                            imageDa.setArtifactRef(artifactTemplateId.getQName());
                            imageDa.setName("CloudImage");

                            TDeploymentArtifacts deploymentArtifacts = nodeTypeImplementation.getDeploymentArtifacts();
                            if (deploymentArtifacts == null) {
                                deploymentArtifacts = new TDeploymentArtifacts();
                                nodeTypeImplementation.setDeploymentArtifacts(deploymentArtifacts);
                            }
                            deploymentArtifacts.getDeploymentArtifact().add(imageDa);
                        } else {
                            logger.info("Could not download image -- the URLs do not exist: \n\t{}\n\t{}",
                                baseUrl + imageFileType, baseUrl + imageDiskType);
                        }
                    } catch (IOException e) {
                        logger.info("Error while downloading image file!", e);
                    }
                }
            } else {
                logger.info("Could not identify code name of given Ubuntu Node Type! {}", nodeType);
            }
        }
    }

    @Override
    public GeneratedArtifacts downloadDependenciesBasedOnArtifact(QName original, IRepository repository) {
        return null;
    }

    private static Map<String, List<String>> initCodeNames() {
        HashMap<String, List<String>> codeNames = new HashMap<>();

        codeNames.put("hirsute", Arrays.asList("21.04", "21"));
        codeNames.put("groovy", Arrays.asList("20.10", "20-10"));
        codeNames.put("focal", Arrays.asList("20LTS", "20-LTS", "20.04", "20.04LTS", "20.04-LTS", "20"));
        codeNames.put("bionic", Arrays.asList("18LTS", "18-LTS", "18.04", "18.04LTS", "18.04-LTS", "18"));
        codeNames.put("xenial", Arrays.asList("16LTS", "16-LTS", "16.04", "16.04LTS", "16.04-LTS", "16"));
        codeNames.put("trusty", Arrays.asList("14LTS", "14-LTS", "14.04", "14.04LTS", "14.04-LTS", "14"));
        codeNames.put("precise", Arrays.asList("12LTS", "12-LTS", "12.04", "12.04LTS", "12.04-LTS", "12"));

        return codeNames;
    }

    private String getCodeName(String componentVersion) {
        String codeName = null;
        for (Map.Entry<String, List<String>> entry : codeNamesToVersion.entrySet()) {
            if (entry.getValue().stream().anyMatch(id -> componentVersion.toLowerCase().equals(id))
                || componentVersion.toLowerCase().equals(entry.getKey())) {
                codeName = entry.getKey();
                break;
            }
        }

        if (codeName == null) {
            for (Map.Entry<String, List<String>> entry : codeNamesToVersion.entrySet()) {
                if (entry.getValue().stream().anyMatch(id -> componentVersion.toLowerCase().startsWith(id))
                    || componentVersion.toLowerCase().startsWith(entry.getKey())) {
                    codeName = entry.getKey();
                    break;
                }
            }
        }

        return codeName;
    }
}
