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

public class Policy extends YAMLElement {
  private String type = "";
  private String description = "";
  private Map<String, Object> properties = new HashMap<String, Object>();
  private String[] targets = new String[0];
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    if (type != null) {
      this.type = type;
    }
  }
  
  public String getDescription() {
    return description;
  }
  
  public void setDescription(String description) {
    if (description != null) {
      this.description = description;
    }
  }
  
  public Map<String, Object> getProperties() {
    return properties;
  }
  
  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
  
  public String[] getTargets() {
    return targets;
  }
  
  public void setTargets(String[] targets) {
    this.targets = targets;
  }

}
