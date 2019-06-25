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

package org.eclipse.winery.repository.rest.datatypes;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UpdateInfo {

    private String nodeTemplateId;
    private String newComponentType;
    private String[][] mappingList;
    private String[] newList;
    private String[] resolvedList;

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

    public String[][] getMappingList() {
        return mappingList;
    }

    public void setMappingList(String[][] mappingList) {
        this.mappingList = mappingList;
    }

    public String[] getNewList() {
        return newList;
    }

    public void setNewList(String[] newList) {
        this.newList = newList;
    }

    public String[] getResolvedList() {
        return resolvedList;
    }

    public void setResolvedList(String[] resolvedList) {
        this.resolvedList = resolvedList;
    }
}
