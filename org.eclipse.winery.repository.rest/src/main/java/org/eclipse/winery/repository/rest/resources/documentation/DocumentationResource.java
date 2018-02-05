/*******************************************************************************
 * Copyright (c) 2012-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.documentation;

import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources._support.AbstractComponentInstanceResource;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import java.util.List;

public class DocumentationResource {

    private final AbstractComponentInstanceResource abstractComponentInstanceResource;
    private final List<TDocumentation> documentation;

    public DocumentationResource(AbstractComponentInstanceResource abstractComponentInstanceResource, List<TDocumentation> documentation) {
        this.abstractComponentInstanceResource = abstractComponentInstanceResource;
        this.documentation = documentation;
    }

    @GET
    public String onGet() {
        if (documentation.isEmpty()) {
            return "";
        } else {
            List<Object> content = documentation.get(0).getContent();
            if (content.isEmpty()) {
                return "";
            }
            return content.get(0).toString();
        }
    }

    @PUT
    public Response onPost(String documentation) {
        this.documentation.clear();
        TDocumentation tDocumentation = new TDocumentation();
        tDocumentation.getContent().add(documentation);
        this.documentation.add(tDocumentation);
        return RestUtils.persist(this.abstractComponentInstanceResource);
    }

}
