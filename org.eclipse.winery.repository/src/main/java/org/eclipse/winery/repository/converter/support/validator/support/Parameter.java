/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter.support.validator.support;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;

public class Parameter extends AbstractParameter<Parameter> {

    @Override
    public Parameter copy() {
        Parameter parameter = new Parameter();
        parameter.getContext().addAll(this.getContext());
        return parameter;
    }

    @Override
    public Parameter self() {
        return this;
    }
}
