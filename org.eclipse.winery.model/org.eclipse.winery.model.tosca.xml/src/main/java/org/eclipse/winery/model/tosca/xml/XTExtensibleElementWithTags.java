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

package org.eclipse.winery.model.tosca.xml;

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
public abstract class XTExtensibleElementWithTags extends XTExtensibleElements implements XHasTags {

    @XmlElementWrapper(name = "Tags")
    @XmlElement(name = "Tag", required = true)
    protected List<XTTag> tags;

    public XTExtensibleElementWithTags(Builder<?> builder) {
        super(builder);
        this.tags = builder.tags;
    }

    @Deprecated
    public XTExtensibleElementWithTags() {
    }

    public List<XTTag> getTags() {
        return tags;
    }

    public void setTags(@Nullable List<XTTag> value) {
        this.tags = value;
    }

    @ADR(11)
    public abstract static class Builder<T extends Builder<T>> extends XTExtensibleElements.Builder<T> {
        
        private List<XTTag> tags; 
        
        public Builder() {
        }

        public Builder(XTExtensibleElements.Builder<?> builder) {
            super(builder);
        }

        public Builder(XTExtensibleElements extensibleElements) {
            this.addDocumentation(extensibleElements.getDocumentation());
            this.addAny(extensibleElements.getAny());
            this.addOtherAttributes(extensibleElements.getOtherAttributes());
        }

        public T addTags(List<XTTag> tags) {
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

        public T addTag(XTTag tag) {
            if (tag == null) {
                return self();
            }

            ArrayList<XTTag> tmp = new ArrayList<>();
            tmp.add(tag);
            return addTags(tmp);
        }

        public T addTag(String key, String value) {
            if (value == null) {
                return self();
            }

            XTTag tag = new XTTag.Builder(key, value).build();
            return addTag(tag);
        }
    }
}
