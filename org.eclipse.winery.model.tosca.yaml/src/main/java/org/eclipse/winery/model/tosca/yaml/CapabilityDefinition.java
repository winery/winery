/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

import java.util.HashMap;
import java.util.Map;

/**
 * Currently not used, but should be used in the future for type definitions!
 * @author Sebi
 */
public class CapabilityDefinition extends YAMLElement {

    private String type = "";
    private Map<String, PropertyDefinition> properties = new HashMap<String, PropertyDefinition>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type != null) {
            this.type = type;
        }
    }

    public Map<String, PropertyDefinition> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, PropertyDefinition> properties) {
        if (properties != null) {
            this.properties = properties;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CapabilityDefinition that = (CapabilityDefinition) o;

        if (!properties.equals(that.properties)) return false;
        if (!type.equals(that.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + properties.hashCode();
        return result;
    }
}
