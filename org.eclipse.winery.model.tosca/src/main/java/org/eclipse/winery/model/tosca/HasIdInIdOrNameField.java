/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.model.tosca;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface HasIdInIdOrNameField {

    /**
     * Returns the id
     */
    @XmlTransient
    @JsonIgnore
    default String getIdFromIdOrNameField() {
        if (this instanceof HasId) {
            return ((HasId) this).getId();
        } else {
            return ((HasName) this).getName();
        }
    }

    /**
     * Sets the id using the given string. In case the class implements HasId, the id is set using HasId.setId.
     * Otherwise, the name attribute is set
     *
     * @param id the id to set
     */
    default void setId(String id) {
        if (this instanceof HasId) {
            ((HasId) this).setId(id);
        } else {
            ((HasName) this).setName(id);
        }
    }
}
