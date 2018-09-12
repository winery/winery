/********************************************************************************
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

package org.eclipse.winery.model.tosca;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tRelationMapping")
public class TRelationMapping implements Serializable {

    @XmlAttribute(name = "id")
    private String id;

    @JsonIdentityReference(alwaysAsId = true)
    @XmlAttribute(name = "detectorNode", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @NonNull
    private TNodeTemplate detectorNode;

    @JsonIdentityReference(alwaysAsId = true)
    @XmlAttribute(name = "refinementNode", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @NonNull
    private TNodeTemplate refinementNode;

    @XmlAttribute(name = "relationType")
    private QName relationType;

    @XmlAttribute(name = "direction")
    private TRelationDirection direction;

    @XmlAttribute(name = "validSourceOrTarget")
    private QName validSourceOrTarget;

    public TNodeTemplate getDetectorNode() {
        return detectorNode;
    }

    public void setDetectorNode(TNodeTemplate detectorNode) {
        this.detectorNode = detectorNode;
    }

    public TNodeTemplate getRefinementNode() {
        return refinementNode;
    }

    public void setRefinementNode(TNodeTemplate refinementNode) {
        this.refinementNode = refinementNode;
    }

    public QName getRelationType() {
        return relationType;
    }

    public void setRelationType(QName relationType) {
        this.relationType = relationType;
    }

    public TRelationDirection getDirection() {
        return direction;
    }

    public void setDirection(TRelationDirection direction) {
        this.direction = direction;
    }

    public QName getValidSourceOrTarget() {
        return validSourceOrTarget;
    }

    public void setValidSourceOrTarget(QName validSourceOrTarget) {
        this.validSourceOrTarget = validSourceOrTarget;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TRelationMapping
            && id.equals(((TRelationMapping) obj).id);
    }
}
