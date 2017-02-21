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


public class Requirement {
  private String node = "";
  private RequirementRelationship relationship;
  private String capability = "";
  private NodeFilter node_filter;
  private int[] occurrences;

  public Requirement() {
    super();
  }

  public Requirement(NodeFilter node_filter) {
    super();
    this.node_filter = node_filter;
  }

  public Requirement(String node) {
    super();
    this.node = node;
  }

  public String getNode() {
    return node;
  }

  public void setNode(String node) {
    if (node != null) {
      this.node = node;
    }
  }

  public RequirementRelationship getRelationship() {
    return relationship;
  }

  public void setRelationship(RequirementRelationship relationship) {
    this.relationship = relationship;
  }

  public String getCapability() {
    return capability;
  }

  public void setCapability(String capability) {
    if (capability != null) {
      this.capability = capability;
    }
  }

  public NodeFilter getNode_filter() {
    return node_filter;
  }

  public void setNode_filter(NodeFilter node_filter) {
    if (node_filter != null) {
      this.node_filter = node_filter;
    }
  }

  public int[] getOccurrences() {
    return occurrences;
  }

  public void setOccurrences(int[] occurrences) {
    if (occurrences != null && occurrences.length == 2) {
      this.occurrences = occurrences;
    }
  }

}