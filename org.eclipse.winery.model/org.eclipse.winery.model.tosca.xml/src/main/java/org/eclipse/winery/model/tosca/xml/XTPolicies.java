/*******************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Policies", propOrder = {
    "policy"
})
public class XTPolicies implements Serializable {

    @XmlElement(name = "Policy", required = true)
    protected List<XTPolicy> policy;

    @Deprecated // required for XML deserialization
    public XTPolicies() { }

    public XTPolicies(List<XTPolicy> policies) {
        this.policy = policies;
    }

    @NonNull
    public List<XTPolicy> getPolicy() {
        if (policy == null) {
            policy = new ArrayList<>();
        }
        return this.policy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XTPolicies policies = (XTPolicies) o;
        return Objects.equals(policy, policies.policy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policy);
    }
}
