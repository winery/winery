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

package org.eclipse.winery.model.tosca;

import java.io.Serializable;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import org.eclipse.jdt.annotation.NonNull;

public abstract class TPrmMapping extends HasId implements Serializable {

    @JsonIdentityReference(alwaysAsId = true)
    @NonNull
    private TEntityTemplate detectorNode;

    @JsonIdentityReference(alwaysAsId = true)
    @NonNull
    private TEntityTemplate refinementNode;

    public TEntityTemplate getDetectorNode() {
        return detectorNode;
    }

    public void setDetectorNode(TEntityTemplate detectorNode) {
        this.detectorNode = detectorNode;
    }

    public TEntityTemplate getRefinementNode() {
        return refinementNode;
    }

    public void setRefinementNode(TEntityTemplate refinementNode) {
        this.refinementNode = refinementNode;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
