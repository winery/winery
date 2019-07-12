/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.threatmodeling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

public class ThreatAssessment {

    private Map<QName, Threat> threats = new HashMap<>();
    private String msg = "";
    private List<String> SVNFs = new ArrayList<>();

    public Map<QName, Threat> getThreats() {
        return threats;
    }

    public void setThreats(Map<QName, Threat> threats) {
        this.threats = threats;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getSVNFs() {
        return SVNFs;
    }

    public void setSVNFs(List<String> SVNFs) {
        this.SVNFs = SVNFs;
    }
}
