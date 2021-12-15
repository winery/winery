/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.yaml;

import java.util.Map;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

public class YTCallOperationActivityDefinition extends YTActivityDefinition implements VisitorNode {
    private final String operation;
    private Map<String, YTParameterDefinition> inputs;

    public YTCallOperationActivityDefinition(String operation) {
        this.operation = operation;
    }

    @Override
    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }

    public String getOperation() {
        return operation;
    }

    public Map<String, YTParameterDefinition> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, YTParameterDefinition> inputs) {
        this.inputs = inputs;
    }
}
