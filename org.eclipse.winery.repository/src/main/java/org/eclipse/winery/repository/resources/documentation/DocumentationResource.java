/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.documentation;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.CollectionsHelper;
import org.eclipse.winery.repository.resources._support.collections.withoutid.EntityWithoutIdResource;

public class DocumentationResource extends EntityWithoutIdResource<TDocumentation> {

    public DocumentationResource(TDocumentation o, int idx, List<TDocumentation> list, IPersistable res) {
        super(o, idx, list, res);
    }

    @PUT
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_PLAIN)
    public Response setValue(String documentation) {
        this.o.getContent().clear();
        this.o.getContent().add(documentation);
        this.list.set(this.idx, this.o);
        return CollectionsHelper.persist(this.res, this.idDetermination, this.o, false);
    }

}
