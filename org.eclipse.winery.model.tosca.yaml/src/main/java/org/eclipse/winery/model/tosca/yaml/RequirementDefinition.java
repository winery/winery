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


public class RequirementDefinition {
  public static final String[] UNBOUNDED_OCCURRENCE = new String [] {"0", "UNBOUNDED"};
  
  private String capability = "";
  private String node = "";
  private String relationship = "";
  private String[] occurrences;

  public RequirementDefinition() {
    super();
  }

  /**
   * @param node
   * @param relationship
   * @param capability
   * @param parseOccurrences
   */
  public RequirementDefinition(Object capability, Object node, Object relationship, String[] occurrences) {
    super();
    if (capability != null) {
      this.capability = capability.toString();
    }
    if (node != null) {
      this.node = node.toString();
    }
    if (relationship != null) {
      this.relationship = relationship.toString();
    }
    this.setOccurrences(occurrences);
//    if (occurrences != null && occurrences.length == 2) {
//      this.occurrences = occurrences;
//    } else {
//      this.occurrences = UNBOUNDED_OCCURRENCE;
//    }
  }

  public String getNode() {
      return node;
  }

  public void setNode(String node) {
      if (node != null) {
          this.node = node;
      }
  }

  public String getRelationship() {
      return relationship;
  }

  public void setRelationship(String relationship) {
      if (relationship != null) {
          this.relationship = relationship;
      }
  }

  public String getCapability() {
      return capability;
  }

  public void setCapability(String capability) {
      if (capability != null) {
          this.capability = capability;
      }
  }

  public String[] getOccurrences() {
      return occurrences;
  }

  public void setOccurrences(String[] occurrences) {
      if (occurrences != null && occurrences.length == 2) {
          this.occurrences = occurrences;
      }
  }
}