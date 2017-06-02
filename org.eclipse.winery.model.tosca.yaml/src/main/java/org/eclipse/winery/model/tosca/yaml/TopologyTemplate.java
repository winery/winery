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
public class TopologyTemplate extends YAMLElement {
    private Map<String, Input> inputs = new HashMap<String, Input>();
    private Map<String, NodeTemplate> node_templates = new HashMap<String, NodeTemplate>();
    private Map<String, Object> relationship_templates = new HashMap<>();
    private Map<String, Group> groups = new HashMap<String, Group>();
    private List<Map<String, Policy>> policies = new ArrayList<>();
    private Map<String, Output> outputs = new HashMap<String, Output>();
    private SubstitutionMapping substitution_mappings;
}
