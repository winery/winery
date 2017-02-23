/**
 * Copyright 2016 ZTE Corporation.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  
 * @author Huabing Zhao
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}