/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial code contribution
 *******************************************************************************/
package org.eclipse.winery.model.tosca;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import io.github.adr.embedded.ADR;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
// see https://stackoverflow.com/q/44789227/873282 for an explanation for this solution
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "fakeJacksonType")
@JsonSubTypes({
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
