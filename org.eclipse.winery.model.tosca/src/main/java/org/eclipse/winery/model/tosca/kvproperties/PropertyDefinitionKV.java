/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca.kvproperties;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "PropertyDefinition")
public class PropertyDefinitionKV implements Serializable {

    private String key;
    private String type;


    public PropertyDefinitionKV() {
        super();
    }

    public PropertyDefinitionKV(String key, String type) {
        super();
        this.setKey(key);
        this.setType(type);
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        this.key = key;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyDefinitionKV that = (PropertyDefinitionKV) o;
        return Objects.equals(key, that.key) &&
            Objects.equals(type, that.type);
    }

    @Override
	public int hashCode() {
		return this.key.hashCode();
	}
}
