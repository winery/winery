/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import io.github.adr.embedded.ADR;
import org.eclipse.jdt.annotation.Nullable;

@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class HasIdAndTags extends HasId implements HasTags {

    @XmlElementWrapper(name = "Tags")
    @XmlElement(name = "Tag", required = true)
    protected List<TTag> tags;

    public HasIdAndTags(Builder<?> builder) {
        super(builder);
        this.tags = builder.tags;
    }

    // required for serialization
    public HasIdAndTags() {
    }

    public List<TTag> getTags() {
        return tags;
    }

    public void setTags(@Nullable List<TTag> value) {
        this.tags = value;
    }

    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> extends HasId.Builder<T> {

        private List<TTag> tags;

        public Builder(String id) {
            super(id);
        }

        public Builder(TExtensibleElements extensibleElements) {
            this.addDocumentation(extensibleElements.getDocumentation());
            this.addAny(extensibleElements.getAny());
            this.addOtherAttributes(extensibleElements.getOtherAttributes());
        }

        public T addTags(List<TTag> tags) {
            if (tags == null || tags.isEmpty()) {
                return self();
            }

            if (this.tags == null) {
                this.tags = tags;
            } else {
                this.tags.addAll(tags);
            }
            return self();
        }

        public T addTag(TTag tag) {
            if (tag == null) {
                return self();
            }

            ArrayList<TTag> tmp = new ArrayList<>();
            tmp.add(tag);
            return addTags(tmp);
        }

        public T addTag(String key, String value) {
            if (value == null) {
                return self();
            }

            TTag tag = new TTag.Builder(key, value).build();
            return addTag(tag);
        }
    }
}
