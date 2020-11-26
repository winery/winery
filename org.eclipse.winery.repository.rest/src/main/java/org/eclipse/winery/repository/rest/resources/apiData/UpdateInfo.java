/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

package org.eclipse.winery.repository.rest.resources.apiData;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.winery.model.tosca.TTopologyTemplate;

@XmlRootElement
public class UpdateInfo {

    private String nodeTemplateId;
    private String newComponentType;
    private List<PropertyMatching> propertyMatchingList;
    private List<String> newList;
    private List<String> resolvedList;
    private boolean saveAfterUpdate;
    private TTopologyTemplate topologyTemplate;

    public String getNodeTemplateId() {
        return nodeTemplateId;
    }

    public void setNodeTemplateId(String nodeTemplateId) {
        this.nodeTemplateId = nodeTemplateId;
    }

    public String getNewComponentType() {
        return newComponentType;
    }

    public void setNewComponentType(String newComponentType) {
        this.newComponentType = newComponentType;
    }

    public List<PropertyMatching> getMappingList() {
        return propertyMatchingList;
    }

    public List<PropertyMatching> getPropertyMatchingList() {
        return propertyMatchingList;
    }

    public void setPropertyMatchingList(List<PropertyMatching> propertyMatchingList) {
        this.propertyMatchingList = propertyMatchingList;
    }

    public List<String> getNewList() {
        return newList;
    }

    public void setNewList(List<String> newList) {
        this.newList = newList;
    }

    public List<String> getResolvedList() {
        return resolvedList;
    }

    public void setResolvedList(List<String> resolvedList) {
        this.resolvedList = resolvedList;
    }

    public boolean isSaveAfterUpdate() {
        return saveAfterUpdate;
    }

    public void setSaveAfterUpdate(boolean saveAfterUpdate) {
        this.saveAfterUpdate = saveAfterUpdate;
    }

    public TTopologyTemplate getTopologyTemplate() {
        return topologyTemplate;
    }

    public void setTopologyTemplate(TTopologyTemplate topologyTemplate) {
        this.topologyTemplate = topologyTemplate;
    }
}
