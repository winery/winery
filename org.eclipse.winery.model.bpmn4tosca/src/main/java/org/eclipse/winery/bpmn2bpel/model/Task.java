/*******************************************************************************
 * Copyright (c) 2015-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.bpmn2bpel.model;

import org.eclipse.winery.bpmn2bpel.model.param.Parameter;

import java.util.*;

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
