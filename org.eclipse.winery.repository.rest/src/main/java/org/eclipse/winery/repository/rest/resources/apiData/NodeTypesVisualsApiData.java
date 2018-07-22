/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.apiData;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.VisualAppearanceResource;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NodeTypesVisualsApiData {

    public String iconUrl;
    public String imageUrl;
    public String color;
    public boolean pattern;
    public QName nodeTypeId;

    public NodeTypesVisualsApiData(VisualAppearanceResource visuals) {
        IRepository repository = RepositoryFactory.getRepository();
        NodeTypeId parent = (NodeTypeId) visuals.getId().getParent();

        this.color = visuals.getBorderColor();
        this.nodeTypeId = parent.getQName();
        this.pattern = repository.getNamespaceManager().isPatternNamespace(parent.getNamespace().getDecoded());

        RepositoryFileReference iconRef = new RepositoryFileReference(visuals.getId(), Filename.FILENAME_SMALL_ICON);
        if (repository.exists(iconRef)) {
            iconUrl = visuals.getAbsoluteURL() + "16x16";
        }

        RepositoryFileReference imageRef = new RepositoryFileReference(visuals.getId(), Filename.FILENAME_BIG_ICON);
        if (repository.exists(imageRef)) {
            imageUrl = visuals.getAbsoluteURL() + "50x50";
        }
    }

    public NodeTypesVisualsApiData() {
    }
}
