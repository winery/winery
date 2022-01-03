/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.interfaces;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


import javax.swing.text.Element;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TRelationshipType;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.InheritedInterfaces;
import org.eclipse.winery.repository.rest.resources.apiData.InterfacesSelectApiData;
import org.eclipse.winery.repository.rest.resources.entitytypes.TopologyGraphElementEntityTypeResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.nodetypes.NodeTypeResource;
import org.eclipse.winery.repository.rest.resources.entitytypes.relationshiptypes.RelationshipTypeResource;

public class InterfacesResource {

    private TopologyGraphElementEntityTypeResource res;
    private List<TInterface> interfaces;
    private String interfaceType;

    public InterfacesResource(TopologyGraphElementEntityTypeResource res, List<TInterface> interfaces, String interfaceType) {
        this.res = res;
        this.interfaces = interfaces;
        this.interfaceType = interfaceType;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response onPost(List<TInterface> interfaceApiData) {
        if (!interfaceApiData.isEmpty()) {
            for (TInterface tInt : interfaceApiData) {
                if (!tInt.getOperations().isEmpty()) {
                    for (TOperation tOp : tInt.getOperations()) {
                        if (tOp.getInputParameters() == null || tOp.getInputParameters().isEmpty()) {
                            tOp.setInputParameters(null);
                        }
                        if (tOp.getOutputParameters() == null || tOp.getOutputParameters().isEmpty()) {
                            tOp.setOutputParameters(null);
                        }
                    }
                } else {
                    return Response.status(Response.Status.BAD_REQUEST).entity("No operation provided!").build();
                }
            }
        }

        List<TInterface> interfaces = new ArrayList<>(interfaceApiData);
        if (this.res instanceof RelationshipTypeResource) {
            TRelationshipType relationshipType = (TRelationshipType) this.res.getElement();
            switch (this.interfaceType) {
                case "source":
                    relationshipType.setSourceInterfaces(interfaces);
                    break;
                case "target":
                    relationshipType.setTargetInterfaces(interfaces);
                    break;
                default:
                    relationshipType.setInterfaces(interfaces);
            }
        } else if (this.res instanceof NodeTypeResource) {
            TNodeType nodeType = (TNodeType) this.res.getElement();
            nodeType.setInterfaces(interfaces);
        } else {
            throw new IllegalStateException("Interfaces are not supported for this element type!");
        }

        return RestUtils.persist(this.res);
    }

    @GET
    @Produces( {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<?> onGet(@QueryParam("selectData") String selectData) {
        if (selectData == null) {
            return this.interfaces;
        }

        List<InterfacesSelectApiData> list = new ArrayList<>();
        for (TInterface item : this.interfaces) {
            List<String> ops = new ArrayList<>();
            for (TOperation op : item.getOperations()) {
                ops.add(op.getName());
            }
            list.add(new InterfacesSelectApiData(item.getName(), ops));
        }

        return list;
    }

    @GET
    @Path("inherited_interfaces")
    @Produces(MediaType.APPLICATION_JSON)
    public List<InheritedInterfaces> getInheritedInterfaces() {
        TExtensibleElements element = this.res.getElement();

        ArrayList<InheritedInterfaces> inheritedInterfaces = new ArrayList<>();
        if (element instanceof TNodeType) {
            TNodeType nodeType = (TNodeType) element;
            if (nodeType.getDerivedFrom() != null) {
                QName parentType = nodeType.getDerivedFrom().getType();
                TNodeType parent = RepositoryFactory.getRepository().getElement(
                    new NodeTypeId(parentType)
                );

                if (parent.getInterfaces() != null) {
                    inheritedInterfaces.add(
                        new InheritedInterfaces(parentType, parent.getInterfaces())
                    );
                }
            }
        }

        return inheritedInterfaces;
    }
}
