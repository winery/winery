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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class Input extends YAMLElement {
  private String type = "";
  @SerializedName("default")
  private Object defaultValue = "";
  private boolean required = false;
  private List<Map<String, Object>> constraints = new ArrayList<Map<String, Object>>();
  private EntrySchema entry_schema;

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    if (type != null) {
      this.type = type;
    }
  }

  public Object getDefault() {
    return this.defaultValue;
  }

  public void setDefault(Object defaultValue) {
    if (defaultValue != null) {
      this.defaultValue = defaultValue;
    }
  }

  public List<Map<String, Object>> getConstraints() {
    return this.constraints;
  }

  public void setConstraints(List<Map<String, Object>> constraints) {
    if (constraints != null) {
      this.constraints = constraints;
    }
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public EntrySchema getEntry_schema() {
    return entry_schema;
  }

  public void setEntry_schema(EntrySchema entry_schema) {
    if (entry_schema != null) {
      this.entry_schema = entry_schema;
    }
  }

}
