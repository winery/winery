/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

import java.util.HashMap;
import java.util.Map;

public class RelationshipType extends YAMLElement {
  private String derived_from = "";
  private Map<String, PropertyDefinition> properties = new HashMap<String, PropertyDefinition>();
  private Map<String, AttributeDefinition> attributes = new HashMap<>();
  private Map<String, Map<String, Map<String, String>>> interfaces =
      new HashMap<String, Map<String, Map<String, String>>>();
  private String[] valid_target_types;

  public String getDerived_from() {
    return this.derived_from;
  }

  public void setDerived_from(String derived_from) {
    if (derived_from != null) {
      this.derived_from = derived_from;
    }
  }

  public Map<String, PropertyDefinition> getProperties() {
    return this.properties;
  }

  public void setProperties(Map<String, PropertyDefinition> properties) {
    if (properties != null) {
      this.properties = properties;
    }
  }

  public Map<String, AttributeDefinition> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, AttributeDefinition> attributes) {
    if (attributes != null) {
      this.attributes = attributes;
    }
  }

  public Map<String, Map<String, Map<String, String>>> getInterfaces() {
    return this.interfaces;
  }

  public void setInterfaces(Map<String, Map<String, Map<String, String>>> interfaces) {
    if (interfaces != null) {
      this.interfaces = interfaces;
    }
  }

  public String[] getValid_target_types() {
    return this.valid_target_types;
  }

  public void setValid_target_types(String[] valid_target_types) {
    this.valid_target_types = valid_target_types;
  }
}
