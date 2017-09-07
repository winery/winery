/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Sebastian Wagner - initial API and implementation
 *     ZTE - support of more gateways
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.winery.bpmn2bpel.model.param.Parameter;

public abstract class Task extends Node {

	private Map<String, Parameter> inputParams = new HashMap<String, Parameter>();

	private Map<String, Parameter> outputParams = new HashMap<String, Parameter>();

	public void addInputParameter(Parameter param) {
		inputParams.put(param.getName(), param);
	}

	public void setInputParameters(List<Parameter> inputParams) {
		Iterator<Parameter> iter = inputParams.iterator();
		while (iter.hasNext()) {
			Parameter param = (Parameter) iter.next();
			this.inputParams.put(param.getName(), param);
		}
	}

	public Parameter getInputParameter(String name) {
		return inputParams.get(name);
	}

	public List<Parameter> getInputParameters() {
		return new ArrayList<Parameter>(inputParams.values());
	}

	public void addOutputParameter(Parameter param) {
		outputParams.put(param.getName(), param);
	}

	public Parameter getOutputParameter(String name) {
		return outputParams.get(name);
	}

	public List<Parameter> getOutputParameters() {
		return new ArrayList<Parameter>(outputParams.values());
	}

	public void setOutputParameters(List<Parameter> outputParams) {
		Iterator<Parameter> iter = outputParams.iterator();
		while (iter.hasNext()) {
			Parameter param = (Parameter) iter.next();
			this.outputParams.put(param.getName(), param);
		}
	}
}
