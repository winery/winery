/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.resources.entitytemplates;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.IHasTypeReference;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.resources._support.collections.withid.EntityWithIdResource;

public class TEntityTemplateResource<E extends TEntityTemplate> extends EntityWithIdResource<E> implements IEntityTemplateResource<E>, IHasTypeReference {

	/**
	 * This constructor is used for both entity templates nested in an component
	 * instance as well as for entity templates being component instances
	 * itself.
	 *
	 * As Java does not support multi-inheritance, we implemented a quick hack
	 * to re-use this class as inner implementation at templates extending
	 * AbstractComponentInstanceResourceDefinitionsBacked
	 */
	public TEntityTemplateResource(IIdDetermination<E> idDetermination, E o, int idx, List<E> list, IPersistable res) {
		super(idDetermination, o, idx, list, res);
	}

	//	public String getId() {
	//		return this.template.getId();
	//	}
	//
	//	public void setId(String id) {
	//		// TODO: There is no check for uniqueness of the given id
	//		this.template.setId(id);
	//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QName getType() {
		return this.o.getType();
	}

	@Path("type")
	@GET
	public String getTypeAsQNameString() {
		return this.getType().toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response setType(QName type) {
		this.o.setType(type);
		return BackendUtils.persist(this.res);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response setType(String typeStr) {
		return this.setType(QName.valueOf(typeStr));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PropertiesResource getPropertiesResource() {
		return new PropertiesResource(this.o, (AbstractComponentInstanceResource) this.res);
	}

}
