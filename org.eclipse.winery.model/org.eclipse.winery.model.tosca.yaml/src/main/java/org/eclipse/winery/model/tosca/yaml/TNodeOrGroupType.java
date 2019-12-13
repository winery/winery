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
package org.eclipse.winery.model.tosca.yaml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tNodeOrGroupType", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.3", propOrder = {
})
@XmlSeeAlso( {TNodeType.class, TGroupType.class})
public class TNodeOrGroupType extends TEntityType {
    public TNodeOrGroupType() {
    }

    public TNodeOrGroupType(Builder builder) {
        super(builder);
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return super.accept(visitor, parameter);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
