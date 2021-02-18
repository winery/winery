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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.repository.backend.IRepository;

public interface SelfContainmentPlugin {

    boolean canHandleNodeType(QName nodeType, IRepository repository);

    boolean canHandleArtifactType(QName artifactType, IRepository repository);

    /**
     * Downloads or generates the respective artifact.
     *
     * @param nodeTypeImplementation - the duplicated original NodeTypeImplementation with self-contained version
     */
    void downloadDependenciesBasedOnNodeType(TNodeTypeImplementation nodeTypeImplementation, IRepository repository);

    /**
     * Downloads or generates the respective artifact.
     *
     * @param original - the QName of the original, NOT-self-contained TOSCA Definition
     * @return - the list of generated ArtifactTemplates to attach as DeploymentArtifacts.
     */
    GeneratedArtifacts downloadDependenciesBasedOnArtifact(QName original, IRepository repository);

    class GeneratedArtifacts {

        public QName artifactToReplaceQName;
        public QName selfContainedArtifactQName;
        public TArtifactTemplate artifactTemplateToReplace;
        public TArtifactTemplate selfContainedArtifactTemplate;
        /**
         * ArtifactTemplate QName as key, ArtifactType as value.
         */
        public List<QName> deploymentArtifactsToAdd = new ArrayList<>();
        public List<QName> deploymentArtifactsToRemove = new ArrayList<>();

        public GeneratedArtifacts(QName artifactToReplace) {
            this.artifactToReplaceQName = artifactToReplace;
        }

        public boolean containsNewElements() {
            return selfContainedArtifactTemplate != null
                || !deploymentArtifactsToAdd.isEmpty()
                || !deploymentArtifactsToRemove.isEmpty();
        }
    }
}
