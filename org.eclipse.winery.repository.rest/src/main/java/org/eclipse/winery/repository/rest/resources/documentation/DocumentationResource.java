/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Lukas Balzer, Nicole Keppler - cleanup for angular
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.documentation;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;

import org.eclipse.winery.model.tosca.TDocumentation;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.AbstractComponentInstanceResource;

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
