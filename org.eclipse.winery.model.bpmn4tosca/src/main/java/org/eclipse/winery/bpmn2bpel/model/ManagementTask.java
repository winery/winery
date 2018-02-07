/*******************************************************************************
 * Copyright (c) 2015-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.bpmn2bpel.model;

import javax.xml.namespace.QName;

public class ManagementTask extends Task {

    private String interfaceName;
    ;

    private QName nodeTemplateId;

    private String nodeOperation;

    public QName getNodeTemplateId() {
        return nodeTemplateId;
    }

    public void setNodeTemplateId(QName nodeTemplateId) {
        this.nodeTemplateId = nodeTemplateId;
    }

    public String getNodeOperation() {
        return nodeOperation;
    }

    public void setNodeOperation(String nodeOperation) {
        this.nodeOperation = nodeOperation;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

}
