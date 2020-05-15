/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/

package org.eclipse.winery.repository.targetallocation.criteria.fulfillpolicies;

import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.tosca.TPolicyTemplate;
import org.eclipse.winery.model.tosca.TPolicyType;
import org.eclipse.winery.model.tosca.kvproperties.PropertyDefinitionKV;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

/**
 * Combines a Policy Template with the property to compare and the
 * operator to do the comparison with.
 */
public class PolicyWrapper {

    private TPolicyTemplate policy;
    private String propertyKey;
    private Object property;
    private String operator;

    /**
     * Policy Template to fulfill.
     */
    public PolicyWrapper(TPolicyTemplate policy, String propertyKey, String operator) {
        this.policy = policy;
        this.propertyKey = propertyKey;
        this.property = extractProperty();
        this.operator = operator;
    }

    /**
     * Policy Template to check with if it's fulfilling.
     */
    public PolicyWrapper(TPolicyTemplate policy, String propertyKey) {
        this.policy = policy;
        this.propertyKey = propertyKey;
        this.property = extractProperty();
    }

    private Object extractProperty() {
        IRepository repository = RepositoryFactory.getRepository();
        PolicyTypeId policyTypeId = new PolicyTypeId(policy.getType());
        TPolicyType policyType = repository.getElement(policyTypeId);

        LinkedHashMap<String, String> properties = ModelUtilities.getPropertiesKV(policy);
        if (properties == null) {
            // FIXME This needs to correctly deal with YamlProperties as well!!
            return null;
        }
        for (Map.Entry<String, String> property : properties.entrySet()) {
            if (property.getKey().equals(propertyKey)) {
                String type = getType(policyType, propertyKey);
                return cast(property.getValue(), type);
            }
        }
        return null;
    }

    // TODO: find library for casting/change when Winery supports typed properties
    // FIXME Assumption here is that the property only comes from a KVProperty and therefore the value is a String
    private Object cast(Object property, String type) {
        switch (type) {
            case "xsd:boolean":
                return Boolean.parseBoolean((String)property);
            case "xsd:double":
                return Double.valueOf((String)property);
            case "xsd:string":
                return property;
            case "xsd:float":
                return Float.parseFloat((String)property);
            case "xsd:decimal":
                return NumberFormat.getInstance().format(property);
            default:
                return property;
        }
    }

    private String getType(TPolicyType policyType, String propertyKey) {
        List<PropertyDefinitionKV> propertyDefinitions = policyType.getWinerysPropertiesDefinition()
            .getPropertyDefinitionKVList()
            .getPropertyDefinitionKVs();
        for (PropertyDefinitionKV propertyDefinition : propertyDefinitions) {
            if (propertyDefinition.getKey().equals(propertyKey)) {
                return propertyDefinition.getType();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicyWrapper that = (PolicyWrapper) o;
        return Objects.equals(policy.getType(), that.policy.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(policy.getType());
    }

    public TPolicyTemplate getPolicy() {
        return policy;
    }

    public void setPolicy(TPolicyTemplate policy) {
        this.policy = policy;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public Object getProperty() {
        return property;
    }

    public void setProperty(Object property) {
        this.property = property;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
