/*******************************************************************************
 * Copyright (c) 2015 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * Required for the BPMN4TOSCA modeler when not running on the same machine
 */
public class CORSFilter implements ContainerResponseFilter {
	
	@Override
	public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {
		ResponseBuilder response = Response.fromResponse(containerResponse.getResponse());
		response.header("Access-Control-Allow-Origin", "*");
		containerResponse.setResponse(response.build());
		return containerResponse;
	}
	
}
