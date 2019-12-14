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

package org.eclipse.winery.repository.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TArtifact;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.repository.datatypes.ids.elements.DirectoryId;

import org.eclipse.jdt.annotation.Nullable;

public class YamlArtifactsSynchronizer {
    final TTopologyTemplate originalTemplate;
    final TTopologyTemplate newTemplate;
    final ServiceTemplateId serviceTemplateId;

    private YamlArtifactsSynchronizer(Builder builder) {
        this.originalTemplate = builder.originalTemplate;
        this.newTemplate = builder.newTemplate;
        this.serviceTemplateId = builder.serviceTemplateId;
    }

    public void synchronizeNodeTemplates() throws IOException {
        for (TNodeTemplate originalNT : this.originalTemplate.getNodeTemplates()) {
            TNodeTemplate newNT = this.newTemplate.getNodeTemplate(originalNT.getId());

            if (newNT == null) {
                this.deleteNodeTemplate(originalNT);
            } else {
                if (originalNT.getArtifacts() != null) {
                    List<TArtifact> toDelete = this.getDeleteList(originalNT.getArtifacts().getArtifact(),
                        newNT.getArtifacts() != null ? newNT.getArtifacts().getArtifact() : null);

                    for (TArtifact artifact : toDelete) {
                        this.deleteYamlArtifact(originalNT, artifact);
                    }
                }
            }
        }
    }

    public void synchronizeRelationshipTemplates() {
        // todo provide logic
    }

    private void deleteNodeTemplate(TNodeTemplate nodeTemplate) throws IOException {
        DirectoryId artifactsDirectory =
            BackendUtils.getYamlArtifactsDirectoryOfNodeTemplate(this.serviceTemplateId, nodeTemplate.getId());
        RepositoryFactory.getRepository().forceDelete(artifactsDirectory);
    }

    private void deleteYamlArtifact(TNodeTemplate nodeTemplate, TArtifact artifact) throws IOException {
        DirectoryId artifactDirectory =
            BackendUtils.getYamlArtifactDirectoryOfNodeTemplate(this.serviceTemplateId, nodeTemplate.getId(), artifact.getId());
        RepositoryFactory.getRepository().forceDelete(artifactDirectory);
    }

    private <T extends TEntityTemplate> List<T> getDeleteList(List<T> originalList, List<T> newList) {
        List<T> toDelete = new ArrayList<>();

        if (newList == null) {
            toDelete.addAll(originalList);
        } else {
            originalList.forEach(nodeTemplate -> {
                if (newList.stream().noneMatch(template -> template.getId().equals(nodeTemplate.getId()))) {
                    toDelete.add(nodeTemplate);
                }
            });
        }

        return toDelete;
    }

    public static class Builder {
        TTopologyTemplate originalTemplate;
        TTopologyTemplate newTemplate;
        ServiceTemplateId serviceTemplateId;

        public Builder setServiceTemplateId(ServiceTemplateId serviceTemplateId) {
            this.serviceTemplateId = serviceTemplateId;
            return self();
        }

        public Builder setOriginalTemplate(@Nullable TTopologyTemplate originalTemplate) {
            this.originalTemplate = originalTemplate;
            return self();
        }

        public Builder setNewTemplate(@Nullable TTopologyTemplate newTemplate) {
            this.newTemplate = newTemplate;
            return self();
        }

        public Builder self() {
            return this;
        }

        public YamlArtifactsSynchronizer build() {
            return new YamlArtifactsSynchronizer(self());
        }
    }
}
