/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeType extends YAMLElement {

  private String derived_from = "";
  private Map<String, PropertyDefinition> properties = new HashMap<>();
  private Map<String, AttributeDefinition> attributes = new HashMap<>();
  private List<Map<String, RequirementDefinition>> requirements = new ArrayList<>();
  private Map<String, CapabilityDefinition> capabilities = new HashMap<>();
  private Map<String, Map<String, Map<String, String>>> interfaces = new HashMap<>();
  private Map<String, ArtifactDefinition> artifacts = new HashMap<>();
  
  public Map<String, ArtifactDefinition> getArtifacts() {
    return artifacts;
  }

  public void setArtifacts(Map<String, ArtifactDefinition> artifacts) {
    this.artifacts = artifacts;
  }

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
      this.attributes = attributes;
  }

  @SuppressWarnings("rawtypes")
  public List<Map<String, RequirementDefinition>> getRequirements() {
    for (Map<String, RequirementDefinition> requirement : this.requirements) {
      for (String name : requirement.keySet()) {
        Object tmp = requirement.get(name);
        if (tmp instanceof Map) {
          Map req_value = (Map) tmp;
          requirement.put(name,  // substitute map with RequirementDefinition
              new RequirementDefinition(req_value.get("capability"),
                  req_value.get("node"),
                  req_value.get("relationship"),
                  parseOccurrences((List)req_value.get("occurrences"))));
        }
      }
      
    }
    return this.requirements;
  }

  /**
   * @param list
   * @return
   */
  @SuppressWarnings("rawtypes")
  private String[] parseOccurrences(List list) {
    if (list == null || list.isEmpty()) {
      return RequirementDefinition.UNBOUNDED_OCCURRENCE;
    }
    
    String[] occurrences = new String[list.size()];
    int i = 0;
    for (Object object : list) {
      occurrences[i++] = object.toString();
    }
    return occurrences;
  }

  public void setRequirements(List<Map<String, RequirementDefinition>> requirements) {
      if (requirements != null) {
          this.requirements = requirements;
      }
  }

  public Map<String, CapabilityDefinition> getCapabilities() {
      return this.capabilities;
  }

  public void setCapabilities(Map<String, CapabilityDefinition> capabilities) {
      if (capabilities != null) {
          this.capabilities = capabilities;
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

      final NodeType nodeType = (NodeType) o;

      if (!this.artifacts.equals(nodeType.artifacts)) {
          return false;
      }
      if (!this.capabilities.equals(nodeType.capabilities)) {
          return false;
      }
      if (!this.derived_from.equals(nodeType.derived_from)) {
          return false;
      }
      if (!this.interfaces.equals(nodeType.interfaces)) {
          return false;
      }
      if (!this.properties.equals(nodeType.properties)) {
          return false;
      }
      if (!this.attributes.equals(nodeType.attributes)) {
          return false;
      }
      if (!this.requirements.equals(nodeType.requirements)) {
          return false;
      }

      return true;
  }

  @Override
  public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + this.derived_from.hashCode();
      result = 31 * result + this.properties.hashCode();
      result = 31 * result + this.attributes.hashCode();
      result = 31 * result + this.requirements.hashCode();
      result = 31 * result + this.capabilities.hashCode();
      result = 31 * result + this.interfaces.hashCode();
      result = 31 * result + this.artifacts.hashCode();
      return result;
  }

}