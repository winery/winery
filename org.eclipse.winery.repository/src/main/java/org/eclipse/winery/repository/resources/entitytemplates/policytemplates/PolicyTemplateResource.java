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
 *     Tino Stadelmaier, Philipp Meyer - rename for id/namespace
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytemplates.policytemplates;

import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.IHasName;
import org.eclipse.winery.repository.resources.entitytemplates.IEntityTemplateResource;
import org.eclipse.winery.repository.resources.entitytemplates.PropertiesResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PolicyTemplateResource extends AbstractComponentInstanceResource implements IEntityTemplateResource<TPolicyTemplate>, IHasName {

	private static final Logger LOGGER = LoggerFactory.getLogger(PolicyTemplateResource.class);


	/**
	 * Constructor has to be public because of test cases
	 */
	public PolicyTemplateResource(PolicyTemplateId id) {
		super(id);
	}

	/**
	 * Convenience method to avoid casting at the caller's side.
	 */
	public TPolicyTemplate getPolicyTemplate() {
		return (TPolicyTemplate) this.getElement();
	}

	@Override
	protected TExtensibleElements createNewElement() {
		return new TPolicyTemplate();
	}

	@Override
	public QName getType() {
		return this.getPolicyTemplate().getType();
	}

	@Override
	public Response setType(QName type) {
		this.getPolicyTemplate().setType(type);
		return BackendUtils.persist(this);
	}

	@Override
	public Response setType(String typeStr) {
		this.getPolicyTemplate().setType(QName.valueOf(typeStr));
		return BackendUtils.persist(this);
	}

	@Override
	public PropertiesResource getPropertiesResource() {
		return new PropertiesResource(this.getPolicyTemplate(), this);
	}

	@Override
	public void copyIdToFields(TOSCAComponentId id) {
		this.getPolicyTemplate().setId(id.getXmlId().getDecoded());
	}

	@Override
	public String getName() {
		String name = this.getPolicyTemplate().getName();
		if (name == null) {
			return this.getPolicyTemplate().getId();
		} else {
			return name;
		}
	}

	@Override
	public Response setName(String name) {
		this.getPolicyTemplate().setName(name);
		return BackendUtils.persist(this);
	}

}
