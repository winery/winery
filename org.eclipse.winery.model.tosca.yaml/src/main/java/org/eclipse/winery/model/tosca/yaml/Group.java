/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

import java.util.HashMap;
import java.util.Map;

/**
 * This implementation can be delayed because it's not used yet.
 */
public class Group extends YAMLElement {
    private String type = "";
    private Map<String, Object> properties = new HashMap<String, Object>();
    private String[] members = new String[0];

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        if (properties == null || properties.isEmpty()) {
            return;
        }
        this.properties = properties;
    }

    public String[] getMembers() {
      return members;
    }

    public void setMembers(String[] members) {
      if (members == null || members.length == 0) {
        return;
      }
      this.members = members;
    }
    
    

}