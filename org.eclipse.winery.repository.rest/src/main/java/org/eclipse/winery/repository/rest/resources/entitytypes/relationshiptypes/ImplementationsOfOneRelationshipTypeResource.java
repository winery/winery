/*******************************************************************************
 * Copyright (c) 2012-2013,2015, 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Nicole Keppler - change getJson for angular2
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.entitytypes.relationshiptypes;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Response;

import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.apiData.converter.QNameConverter;
import org.eclipse.winery.repository.rest.resources.entitytypes.ImplementationsOfOneType;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImplementationsOfOneRelationshipTypeResource extends ImplementationsOfOneType {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImplementationsOfOneRelationshipTypeResource.class);


	public ImplementationsOfOneRelationshipTypeResource(RelationshipTypeId typeId) {
		super(typeId);
	}


	/**
	 * required by implementations.jsp
	 *
	 * Method similar top the one of ImplementationsOfOneNodeTypeResource
	 *
	 * @return for each node type implementation implementing the associated node type
	 */
	@Override
	public String getImplementationsTableData() {
		String res;
		JsonFactory jsonFactory = new JsonFactory();
		StringWriter tableDataSW = new StringWriter();
		try {
			JsonGenerator jGenerator = jsonFactory.createGenerator(tableDataSW);
			jGenerator.writeStartArray();

			Collection<RelationshipTypeImplementationId> allNTIids = RepositoryFactory.getRepository().getAllElementsReferencingGivenType(RelationshipTypeImplementationId.class, this.getTypeId().getQName());
			for (RelationshipTypeImplementationId ntiID : allNTIids) {
				jGenerator.writeStartArray();
				jGenerator.writeString(ntiID.getNamespace().getDecoded());
				jGenerator.writeString(ntiID.getXmlId().getDecoded());
				jGenerator.writeEndArray();
			}
			jGenerator.writeEndArray();
			jGenerator.close();
			tableDataSW.close();
			res = tableDataSW.toString();
		} catch (Exception e) {
			ImplementationsOfOneRelationshipTypeResource.LOGGER.error(e.getMessage(), e);
			res = "[]";
		}
		return res;
	}

	@Override
	public String getType() {
		return "relationshiptype";
	}

	@Override
	public String getTypeStr() {
		return "Relationship Type";
	}

	@Override
	public Response getJSON() {
		Collection<RelationshipTypeImplementationId> allImplementations = RepositoryFactory.getRepository().getAllElementsReferencingGivenType(RelationshipTypeImplementationId.class, this.getTypeId().getQName());
		List<QNameApiData> res = new ArrayList<>(allImplementations.size());
		QNameConverter adapter = new QNameConverter();
		for (RelationshipTypeImplementationId id : allImplementations) {
			res.add(adapter.marshal(id.getQName()));
		}
		return Response.ok().entity(res).build();
	}
}
