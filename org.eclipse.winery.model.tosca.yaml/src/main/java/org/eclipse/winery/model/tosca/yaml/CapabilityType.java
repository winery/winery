/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

import java.util.HashMap;
import java.util.Map;

public class CapabilityType extends YAMLElement {
  private Map<String, PropertyDefinition> properties = new HashMap<String, PropertyDefinition>();
  private String derived_from = "";

  public Map<String, PropertyDefinition> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, PropertyDefinition> properties) {
    this.properties = properties;
  }

  public String getDerived_from() {
    return derived_from;
  }

  public void setDerived_from(String derived_from) {
    if (derived_from != null) {
      this.derived_from = derived_from;
    }
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    CapabilityType that = (CapabilityType) o;

    if (!derived_from.equals(that.derived_from)) return false;
    if (!properties.equals(that.properties)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + properties.hashCode();
    result = 31 * result + derived_from.hashCode();
    return result;
  }

}