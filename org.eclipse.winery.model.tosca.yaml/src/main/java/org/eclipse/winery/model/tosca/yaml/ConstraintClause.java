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

public class ConstraintClause {
  private Object equal;
  private Object greater_than;
  private Object greater_or_equal;
  private Object less_than;
  private Object less_or_equal;
  private Object[] in_range;
  private Object[] valid_values;
  private int length;
  private int min_length;
  private int max_length;
  private String pattern;
  
  
  public ConstraintClause() {
    super();
  }

  public ConstraintClause(Object equal) {
    super();
    this.equal = equal;
  }
  
  public Object getEqual() {
    return equal;
  }
  public void setEqual(Object equal) {
    this.equal = equal;
  }
  public Object getGreater_than() {
    return greater_than;
  }
  public void setGreater_than(Object greater_than) {
    this.greater_than = greater_than;
  }
  public Object getGreater_or_equal() {
    return greater_or_equal;
  }
  public void setGreater_or_equal(Object greater_or_equal) {
    this.greater_or_equal = greater_or_equal;
  }
  public Object getLess_than() {
    return less_than;
  }
  public void setLess_than(Object less_than) {
    this.less_than = less_than;
  }
  public Object getLess_or_equal() {
    return less_or_equal;
  }
  public void setLess_or_equal(Object less_or_equal) {
    this.less_or_equal = less_or_equal;
  }
  public Object[] getIn_range() {
    return in_range;
  }
  public void setIn_range(Object[] in_range) {
    this.in_range = in_range;
  }
  public Object[] getValid_values() {
    return valid_values;
  }
  public void setValid_values(Object[] valid_values) {
    this.valid_values = valid_values;
  }
  public int getLength() {
    return length;
  }
  public void setLength(int length) {
    this.length = length;
  }
  public int getMin_length() {
    return min_length;
  }
  public void setMin_length(int min_length) {
    this.min_length = min_length;
  }
  public int getMax_length() {
    return max_length;
  }
  public void setMax_length(int max_length) {
    this.max_length = max_length;
  }
  public String getPattern() {
    return pattern;
  }
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }
  
}
