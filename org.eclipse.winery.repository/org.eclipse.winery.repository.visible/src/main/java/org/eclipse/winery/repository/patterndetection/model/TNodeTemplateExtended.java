/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.patterndetection.model;

import org.eclipse.winery.model.tosca.TNodeTemplate;

public class TNodeTemplateExtended {

    private TNodeTemplate tNodeTemplate;
    private String label;
    private String keyword;


    public TNodeTemplateExtended() {

    }

    public TNodeTemplateExtended(TNodeTemplate tNodeTemplate, String label, String keyword) {
        this.tNodeTemplate = tNodeTemplate;
        this.label = label;
        this.keyword = keyword;
    }

    public TNodeTemplate getNodeTemplate() {
        return tNodeTemplate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
