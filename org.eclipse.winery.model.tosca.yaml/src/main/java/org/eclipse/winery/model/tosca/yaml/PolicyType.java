/**
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.winery.model.tosca.yaml;

import java.util.HashMap;
import java.util.Map;

public class PolicyType extends YAMLElement {

    private String derived_from = "";
    private String version = "";
    private String description = "";
    private Map<String, PropertyDefinition> properties = new HashMap<String, PropertyDefinition>();
    private String[] targets = new String[0];
    
    public String getVesion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDerived_from() {
        return derived_from;
    }
    public void setDerived_from(String derived_from) {
        this.derived_from = derived_from;
    }
    public Map<String, PropertyDefinition> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, PropertyDefinition> properties) {
        this.properties = properties;
    }
    public String[] getTargets() {
        return targets;
    }
    public void setTargets(String[] targets) {
        this.targets = targets;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final PolicyType policyType = (PolicyType) o;

        if (!this.derived_from.equals(policyType.derived_from)) {
            return false;
        }
        if (!this.version.equals(policyType.version)) {
            return false;
        }
        if (!this.description.equals(policyType.description)) {
            return false;
        }
        if (!this.properties.equals(policyType.properties)) {
            return false;
        }
        if (!this.targets.equals(policyType.targets)) {
            return false;
        }

        return true;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.derived_from.hashCode();
        result = 31 * result + this.version.hashCode();
        result = 31 * result + this.description.hashCode();
        result = 31 * result + this.properties.hashCode();
        result = 31 * result + this.targets.hashCode();
        return result;
    }
}
