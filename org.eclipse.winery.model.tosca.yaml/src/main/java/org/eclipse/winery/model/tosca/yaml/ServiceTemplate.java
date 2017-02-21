/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceTemplate extends YAMLElement {
  private String tosca_definitions_version = "";
  private Map<String, String> metadata = new HashMap<>();
  private Map<String, Object> dsl_definitions = new HashMap<>();
  private List<String> imports = new ArrayList<String>();
  private Map<String, ArtifactType> artifact_types = new HashMap<String, ArtifactType>();
  private Map<String, DataType> data_types = new HashMap<>();
  private Map<String, CapabilityType> capability_types = new HashMap<String, CapabilityType>();
  private Map<String, Object> interface_types = new HashMap<>();
  private Map<String, RelationshipType> relationship_types = new HashMap<String, RelationshipType>();
  private Map<String, NodeType> node_types = new HashMap<String, NodeType>();
  private Map<String, Object> group_types = new HashMap<>();
  private Map<String, PolicyType> policy_types = new HashMap<>();

  private TopologyTemplate topology_template;
  private Map<String, Plan> plans = new HashMap<>();

//  private String tosca_default_namespace = "";
  private String template_name = "";
  private String template_author = "";
  private String template_version = "";

  public Map<String, String> getMetadata() {
      return metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
      this.metadata = metadata;
  }

  public Map<String, Object> getDsl_definitions() {
      return dsl_definitions;
  }

  public void setDsl_definitions(Map<String, Object> dsl_definitions) {
      this.dsl_definitions = dsl_definitions;
  }

  public Map<String, DataType> getData_types() {
      return data_types;
  }

  public void setData_types(Map<String, DataType> data_types) {
      this.data_types = data_types;
  }

  public Map<String, Object> getInterface_types() {
      return interface_types;
  }

  public void setInterface_types(Map<String, Object> interface_types) {
      this.interface_types = interface_types;
  }

  public Map<String, Object> getGroup_types() {
      return group_types;
  }

  public void setGroup_types(Map<String, Object> group_types) {
      this.group_types = group_types;
  }

  public Map<String, PolicyType> getPolicy_types() {
      return policy_types;
  }

  public void setPolicy_types(Map<String, PolicyType> policy_types) {
      this.policy_types = policy_types;
  }
  
  public TopologyTemplate getTopology_template() {
      return topology_template;
  }

public void setTopology_template(TopologyTemplate topology_template) {
  this.topology_template = topology_template;
}

public Map<String, Plan> getPlans() {
  return plans;
}

public void setPlans(Map<String, Plan> plans) {
  if (plans != null) {
    this.plans = plans;
  }
  }

  public void setTosca_definitions_version(String tosca_definitions_version) {
      if (tosca_definitions_version != null) {
          this.tosca_definitions_version = tosca_definitions_version;
      }
  }

  public String getTosca_definitions_version() {
      return this.tosca_definitions_version;
  }

//  public void setTosca_default_namespace(String tosca_default_namespace) {
//      if (tosca_default_namespace != null) {
//          this.tosca_default_namespace = tosca_default_namespace;
//      }
//  }
//
//  public String getTosca_default_namespace() {
//      return this.tosca_default_namespace;
//  }

  public void setTemplate_name(String template_name) {
      if (template_name != null) {
          this.template_name = template_name;
      }
  }

  public String getTemplate_name() {
      return this.template_name;
  }

  public void setTemplate_author(String template_author) {
      if (template_author != null) {
          this.template_author = template_author;
      }
  }

  public String getTemplate_author() {
      return this.template_author;
  }

  public void setTemplate_version(String template_version) {
      if (template_version != null) {
          this.template_version = template_version;
      }
  }

  public String getTemplate_version() {
      return this.template_version;
  }

  public void setImports(List<String> imports) {
      if (imports != null) {
          this.imports = imports;
      }
  }

  public List<String> getImports() {
      return this.imports;
  }

  public void setNode_types(Map<String, NodeType> node_types) {
      if (node_types != null) {
          this.node_types = node_types;
      }
  }

  public Map<String, NodeType> getNode_types() {
      return this.node_types;
  }

  public void setCapability_types(Map<String, CapabilityType> capability_types) {
      if (capability_types != null) {
          this.capability_types = capability_types;
      }
  }

  public Map<String, CapabilityType> getCapability_types() {
      return this.capability_types;
  }

  public void setRelationship_types(Map<String, RelationshipType> relationship_types) {
      if (relationship_types != null) {
          this.relationship_types = relationship_types;
      }
  }

  public Map<String, RelationshipType> getRelationship_types() {
      return this.relationship_types;
  }

  public void setArtifact_types(Map<String, ArtifactType> artifact_types) {
      if (artifact_types != null) {
          this.artifact_types = artifact_types;
      }
  }

  public Map<String, ArtifactType> getArtifact_types() {
      return this.artifact_types;
  }
}