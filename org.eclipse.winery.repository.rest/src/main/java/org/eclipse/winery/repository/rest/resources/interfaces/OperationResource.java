/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.rest.resources.interfaces;

import java.util.List;

import javax.ws.rs.Path;

import org.eclipse.winery.model.tosca.TInterface;
import org.eclipse.winery.model.tosca.TOperation;
import org.eclipse.winery.model.tosca.TOperation.InputParameters;
import org.eclipse.winery.model.tosca.TOperation.OutputParameters;
import org.eclipse.winery.repository.rest.resources._support.IPersistable;
import org.eclipse.winery.repository.rest.resources._support.collections.IIdDetermination;
import org.eclipse.winery.repository.rest.resources._support.collections.withid.EntityWithIdResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationResource extends EntityWithIdResource<TOperation> {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperationResource.class);


	public OperationResource(IIdDetermination<TOperation> idDetermination, TOperation o, int idx, List<TOperation> list, IPersistable res) {
		super(idDetermination, o, idx, list, res);
	}

	/**
	 * @return TOperation object for the corresponding object of operationName
	 *         in the operation list contained in the given interface. null if
	 *         interface could not be found in list
	 */
	public static TOperation getTOperation(String operationName, TInterface iface) {
		List<TOperation> operationList = iface.getOperation();
		for (TOperation op : operationList) {
			if (op.getName().equals(operationName)) {
				return op;
			}
		}
		return null;
	}

	@Path("inputparameters/")
	public ParametersResource getInputparameters() {
		InputParameters inputParameters = this.o.getInputParameters();
		if (inputParameters == null) {
			inputParameters = new InputParameters();
			this.o.setInputParameters(inputParameters);
		}
		return new ParametersResource(inputParameters.getInputParameter(), this.res);
	}

	@Path("outputparameters/")
	public ParametersResource getOutputparameters() {
		OutputParameters outputParameters = this.o.getOutputParameters();
		if (outputParameters == null) {
			outputParameters = new OutputParameters();
			this.o.setOutputParameters(outputParameters);
		}
		return new ParametersResource(outputParameters.getOutputParameter(), this.res);
	}
}
