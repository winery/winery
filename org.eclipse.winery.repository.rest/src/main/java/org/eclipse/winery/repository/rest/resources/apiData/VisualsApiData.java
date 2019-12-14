/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

import javax.ws.rs.core.UriInfo;
import javax.xml.namespace.QName;

import org.eclipse.winery.repository.common.RepositoryFileReference;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.rest.resources._support.GenericVisualAppearanceResource;

public class VisualsApiData {

    public String iconUrl;
    public String imageUrl;
    public String color;
    public boolean pattern;
    public QName typeId;

    public VisualsApiData(GenericVisualAppearanceResource visuals, UriInfo uriInfo) {
        IRepository repository = RepositoryFactory.getRepository();
        DefinitionsChildId parent = (DefinitionsChildId) visuals.getId().getParent();

        this.color = visuals.getColor();
        this.typeId = parent.getQName();
        this.pattern = repository.getNamespaceManager().isPatternNamespace(parent.getNamespace().getDecoded());

        RepositoryFileReference iconRef = new RepositoryFileReference(visuals.getId(), Filename.FILENAME_SMALL_ICON);
        if (repository.exists(iconRef)) {
            if (uriInfo != null) {
                iconUrl = visuals.getAbsoluteURL(uriInfo) + "16x16";
            } else {
                iconUrl = visuals.getAbsoluteURL() + "16x16";
            }
        }

        RepositoryFileReference imageRef = new RepositoryFileReference(visuals.getId(), Filename.FILENAME_BIG_ICON);
        if (repository.exists(imageRef)) {
            if (uriInfo != null) {
                imageUrl = visuals.getAbsoluteURL(uriInfo) + "50x50";
            } else {
                imageUrl = visuals.getAbsoluteURL() + "50x50";
            }
        }
    }

    public VisualsApiData(GenericVisualAppearanceResource visuals) {
        this(visuals, null);
    }

    public VisualsApiData() {
    }
}
