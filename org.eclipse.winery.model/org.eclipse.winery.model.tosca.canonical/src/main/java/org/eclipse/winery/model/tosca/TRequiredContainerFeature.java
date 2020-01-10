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
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

/**
 * <p>Java class for tRequiredContainerFeature complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="tRequiredContainerFeature">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="feature" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
public class TRequiredContainerFeature implements Serializable {

    protected String feature;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TRequiredContainerFeature)) return false;
        TRequiredContainerFeature that = (TRequiredContainerFeature) o;
        return Objects.equals(feature, that.feature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feature);
    }

    /**
     * Gets the value of the feature property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getFeature() {
        return feature;
    }

    /**
     * Sets the value of the feature property.
     *
     * @param value allowed object is {@link String }
     */
    public void setFeature(String value) {
        this.feature = value;
    }
}
