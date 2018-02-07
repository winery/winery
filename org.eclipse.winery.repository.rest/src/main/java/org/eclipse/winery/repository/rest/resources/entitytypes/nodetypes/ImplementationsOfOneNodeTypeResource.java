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
package org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.resources.apiData.QNameApiData;
import org.eclipse.winery.repository.rest.resources.apiData.converter.QNameConverter;
import org.eclipse.winery.repository.rest.resources.entitytypes.ImplementationsOfOneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImplementationsOfOneNodeTypeResource extends ImplementationsOfOneType {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImplementationsOfOneNodeTypeResource.class);


    /**
     * The constructor is different from the usual constructors as this resource
     * does NOT store own data, but retrieves its data solely from the
     * associated node type
     *
     * @param nodeTypeId the node type id, where this list of implementations
     *                   belongs to
     */
    public ImplementationsOfOneNodeTypeResource(NodeTypeId nodeTypeId) {
        super(nodeTypeId);
    }

    /**
     * required by implementations.jsp
     *
     * @return for each node type implementation implementing the associated
     * node type
     */
    @Override
    public String getImplementationsTableData() {
        String res;
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter tableDataSW = new StringWriter();
        try {
            JsonGenerator jGenerator = jsonFactory.createGenerator(tableDataSW);
            jGenerator.writeStartArray();

            Collection<NodeTypeImplementationId> allNodeTypeImplementations = RepositoryFactory.getRepository().getAllElementsReferencingGivenType(NodeTypeImplementationId.class, this.getTypeId().getQName());
            for (NodeTypeImplementationId ntiID : allNodeTypeImplementations) {
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
            ImplementationsOfOneNodeTypeResource.LOGGER.error(e.getMessage(), e);
            res = "[]";
        }
        return res;
    }

    @Override
    public String getType() {
        return "nodetype";
    }

    @Override
    public String getTypeStr() {
        return "Node Type";
    }

    @Override
    public Response getJSON() {
        Collection<NodeTypeImplementationId> allImplementations = RepositoryFactory.getRepository().getAllElementsReferencingGivenType(NodeTypeImplementationId.class, this.getTypeId().getQName());
        List<QNameApiData> res = new ArrayList<>(allImplementations.size());
        QNameConverter adapter = new QNameConverter();
        for (NodeTypeImplementationId id : allImplementations) {
            res.add(adapter.marshal(id.getQName()));
        }
        return Response.ok().entity(res).build();
    }

}
