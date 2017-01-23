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
package org.eclipse.winery.repository.resources.entitytypes.nodetypes.reqandcapdefs;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TConstraint;
import org.eclipse.winery.model.tosca.TRequirementDefinition;
import org.eclipse.winery.model.tosca.TRequirementDefinition.Constraints;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.resources.entitytypes.nodetypes.NodeTypeResource;

public final class RequirementDefinitionResource extends AbstractReqOrCapDefResource<TRequirementDefinition> {

	private TRequirementDefinition reqDef;


	/**
	 * Constructor has to follow the pattern of EnetityTResource as the
	 * constructor is invoked by reflection in EntityWithIdcollectionResource
	 *
	 * @param res the resource this req def is nested in. Has to be of Type
	 *            "NodeTypeResource". Due to the implementation of
	 *            org.eclipse.winery .repository.resources._support.collections.
	 *            withid.EntityWithIdCollectionResource
	 *            .getEntityResourceInstance(EntityT, int), we have to use
	 *            "AbstractComponentInstanceResource" as type
	 */
	public RequirementDefinitionResource(IIdDetermination<TRequirementDefinition> idDetermination, TRequirementDefinition reqDef, int idx, List<TRequirementDefinition> list, AbstractComponentInstanceResource res) {
		super(idDetermination, reqDef, idx, list, (NodeTypeResource) res, RequirementDefinitionResource.getConstraints(reqDef));
		this.reqDef = reqDef;
	}

	/**
	 * Quick fix to avoid internal server error when opening
	 * RequirementDefinitions Tab
	 */
	public RequirementDefinitionResource(IIdDetermination<TRequirementDefinition> idDetermination, TRequirementDefinition reqDef, int idx, List<TRequirementDefinition> list, IPersistable res) {
		this(idDetermination, reqDef, idx, list, (AbstractComponentInstanceResource) res);
	}

	/**
	 * Fetch the list of constraints from the given definition. If the list does
	 * not exist, the list is created an stored in the given def
	 */
	public static List<TConstraint> getConstraints(TRequirementDefinition def) {
		Constraints constraints = def.getConstraints();
		if (constraints == null) {
			constraints = new Constraints();
			def.setConstraints(constraints);
		}
		return constraints.getConstraint();
	}

	public QName getType() {
		return this.reqDef.getRequirementType();
	}

	@PUT
	@Path("type")
	public Response setType(@FormParam(value = "type") String value) {
		QName qname = QName.valueOf(value);
		this.reqDef.setRequirementType(qname);
		return BackendUtils.persist(this.parent);
	}

	@Override
	public String getId(TRequirementDefinition e) {
		return e.getName();
	}
}
