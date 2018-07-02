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
package org.eclipse.winery.model.tosca;

import com.fasterxml.jackson.annotation.*;
import io.github.adr.embedded.ADR;

import javax.xml.namespace.QName;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
// see https://stackoverflow.com/q/44789227/873282 for an explanation for this solution
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "fakeJacksonType")
@JsonSubTypes( {
    @JsonSubTypes.Type(value = TRequirement.class, name = "requirement"),
    @JsonSubTypes.Type(value = TCapability.class, name = "capability"),
    @JsonSubTypes.Type(value = TNodeTemplate.class, name = "nodetemplate")
})
public abstract class RelationshipSourceOrTarget extends TEntityTemplate {

    public RelationshipSourceOrTarget() {
        super();
    }

    public RelationshipSourceOrTarget(String id) {
        super(id);
    }

    public RelationshipSourceOrTarget(Builder builder) {
        super(builder);
    }

    @JsonIgnore
    public abstract String getFakeJacksonType();

    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> extends TEntityTemplate.Builder<T> {
        public Builder(String id, QName type) {
            super(id, type);
        }

        public Builder(TEntityTemplate entityTemplate) {
            super(entityTemplate);
        }
    }
}
