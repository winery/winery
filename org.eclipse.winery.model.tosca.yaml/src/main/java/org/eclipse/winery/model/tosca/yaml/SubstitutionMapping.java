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

/**
 * @author 10090474
 *
 */

public class SubstitutionMapping {
  private String node_type;
  private Map<String, String[]> requirements = new HashMap<>();
  private Map<String, String[]> capabilities = new HashMap<>();
  
  public String getNode_type() {
    return node_type;
  }
  
  public void setNode_type(String node_type) {
    this.node_type = node_type;
  }
  
  public Map<String, String[]> getRequirements() {
    return requirements;
  }
  
  public void setRequirements(Map<String, String[]> requirements) {
    if (requirements != null && requirements.isEmpty()) {
      return;
    }
    this.requirements = requirements;
  }
  
  public Map<String, String[]> getCapabilities() {
    return capabilities;
  }
  
  public void setCapabilities(Map<String, String[]> capabilities) {
    if (capabilities != null && capabilities.isEmpty()) {
      return;
    }
    this.capabilities = capabilities;
  }


}
