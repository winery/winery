/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeTemplate extends YAMLElement {
  private String type = "";
  private Map<String, Object> properties = new HashMap<String, Object>();
  private Map<String, Object> attributes = new HashMap<String, Object>();
  private List<Map<String, Object>> requirements = new ArrayList<>();  // May be string or org.eclipse.winery.repository.ext.yamlmodel.Requirement
  private Map<String, Capability> capabilities = new HashMap<>();
  private Map<String, String> interfaces = new HashMap<String, String>();
  private Map<String, Object> artifacts = new HashMap<>();  // Maybe String or ArtifactDefinition.

  public void setType(String type) {
    if (type != null) {
      this.type = type;
    }
  }

  public String getType() {
    return this.type;
  }

  public void setProperties(Map<String, Object> properties) {
    if (properties != null) {
      this.properties = properties;
    }
  }

  public Map<String, Object> getProperties() {
    return this.properties;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public void setRequirements(List<Map<String, Object>> requirements) {
    if (requirements != null) {
      this.requirements = requirements;
    }
  }

  public List<Map<String, Object>> getRequirements() {
    return this.requirements;
  }

  public void setCapabilities(Map<String, Capability> capabilities) {
    if (capabilities != null) {
      this.capabilities = capabilities;
    }
  }

  public Map<String, Capability> getCapabilities() {
    return this.capabilities;
  }

  public Map<String, String> getInterfaces() {
    return this.interfaces;
  }

  public void setInterfaces(Map<String, String> interfaces) {
    if (interfaces != null) {
      this.interfaces = interfaces;
    }
  }
  
  public Map<String, Object> getArtifacts() {
    return artifacts;
  }

  public void setArtifacts(Map<String, Object> artifacts) {
    this.artifacts = artifacts;
  }
}