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
package org.eclipse.winery.repository.resources.entitytypes.policytypes;

import java.util.SortedSet;

import javax.ws.rs.Path;

import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.repository.datatypes.select2.Select2OptGroup;
import org.eclipse.winery.repository.resources.EntityTypeResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PolicyTypeResource extends EntityTypeResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(PolicyTypeResource.class);


	/**
	 * Constructor has to be public because of test cases
	 */
	public PolicyTypeResource(PolicyTypeId id) {
		super(id);
	}

	/**
	 * Convenience method to avoid casting at the caller's side.
	 */
	public TPolicyType getPolicyType() {
		return (TPolicyType) this.getElement();
	}

	@Override
	protected TExtensibleElements createNewElement() {
		return new TPolicyType();
	}

	@Path("appliesto/")
	public AppliesToResource getAppliesTo() {
		return new AppliesToResource(this);
	}

	@Path("language/")
	public LanguageResource getLanguage() {
		return new LanguageResource(this);
	}

	@Override
	public SortedSet<Select2OptGroup> getListOfAllInstances() {
		return this.getListOfAllInstances(PolicyTemplateId.class);
	}

}
