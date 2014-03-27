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
package org.eclipse.winery.repository.resources.entitytemplates;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.servicetemplates.topologytemplates.RelationshipTemplateResource;

import com.sun.jersey.api.NotFoundException;

/**
 * This resource models the list of TEntityTemplates
 */
public class TEntityTemplatesResource<T extends TEntityTemplate> {
	
	private final List<T> templateList;
	protected final AbstractComponentInstanceResource res;
	private final Class<? extends TEntityTemplateResource<T>> templateResourceClazz;
	private final Class<T> clazz;
	
	
	/**
	 * 
	 * @param templateList the list to represent
	 * @param clazz the class of a single element of a list
	 * @param templateResourceClazz the resource class, which represents a
	 *            single element
	 * @param res the resource where the list is nested in
	 */
	public TEntityTemplatesResource(List<T> templateList, Class<T> clazz, Class<? extends TEntityTemplateResource<T>> templateResourceClazz, AbstractComponentInstanceResource res) {
		this.templateList = templateList;
		this.clazz = clazz;
		this.templateResourceClazz = templateResourceClazz;
		this.res = res;
	}
	
	@Path("{id}/")
	public TEntityTemplateResource<T> getTEntityTemplateResource(@PathParam("id") String id) {
		id = Util.URLdecode(id);
		T template = null;
		for (T t : this.templateList) {
			if (t.getId().equals(id)) {
				template = t;
				break;
			}
		}
		if (template == null) {
			throw new NotFoundException();
		}
		Constructor<? extends TEntityTemplateResource<T>> constructor;
		try {
			constructor = this.templateResourceClazz.getConstructor(this.clazz, List.class, int.class, AbstractComponentInstanceResource.class);
		} catch (Exception e) {
			throw new IllegalStateException("Could not get constructor for TElemenetInstanceResource", e);
		}
		TEntityTemplateResource<T> subRes;
		try {
			subRes = constructor.newInstance(null, this.res);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalStateException("Could not instantiate TElemenetInstanceResource", e);
		}
		return subRes;
	}
	
	/**
	 * (no more?) required by topologytemplateedit.jsp
	 */
	public Collection<RelationshipTemplateResource> getAll() {
		throw new IllegalStateException("This is outdated, isn't it?");
		//		Collection<RelationshipTemplateResource> res = new HashSet<RelationshipTemplateResource>();
		//		@SuppressWarnings("unchecked")
		//		SortedSet<RelationshipTemplateId> nestedIds = (SortedSet<RelationshipTemplateId>) (SortedSet<?>) Repository.INSTANCE.getNestedIds(this.id, RelationshipTemplateId.class);
		//		for (RelationshipTemplateId id : nestedIds) {
		//			RelationshipTemplateResource r = new RelationshipTemplateResource(id);
		//			res.add(r);
		//		}
		//		return res;
	}
}
