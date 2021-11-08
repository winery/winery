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

package org.eclipse.winery.model.tosca.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.winery.model.tosca.xml.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tOperation", propOrder = {
    "inputParameters",
    "outputParameters"
})
public class XTOperation extends XTExtensibleElements {

    @XmlElementWrapper(name = "InputParameters")
    @XmlElement(name = "InputParameter", required = true)
    protected List<XTParameter> inputParameters;

    @XmlElementWrapper(name = "OutputParameters")
    @XmlElement(name = "OutputParameter", required = true)
    protected List<XTParameter> outputParameters;

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

    @Deprecated // required for XML deserialization
    public XTOperation() {
    }

    public XTOperation(Builder builder) {
        super(builder);
        this.inputParameters = builder.inputParameters;
        this.outputParameters = builder.outputParameters;
        this.name = builder.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XTOperation)) return false;
        if (!super.equals(o)) return false;
        XTOperation that = (XTOperation) o;
        return Objects.equals(inputParameters, that.inputParameters) &&
            Objects.equals(outputParameters, that.outputParameters) &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), inputParameters, outputParameters, name);
    }

    public List<XTParameter> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(List<XTParameter> value) {
        this.inputParameters = value;
    }

    public List<XTParameter> getOutputParameters() {
        return outputParameters;
    }

    public void setOutputParameters(List<XTParameter> value) {
        this.outputParameters = value;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String value) {
        Objects.requireNonNull(value);
        this.name = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public static class Builder extends XTExtensibleElements.Builder<Builder> {
        private final String name;
        private List<XTParameter> inputParameters;
        private List<XTParameter> outputParameters;

        public Builder(String name) {
            this.name = name;
        }

        public Builder setInputParameters(List<XTParameter> inputParameters) {
            this.inputParameters = inputParameters;
            return this;
        }

        public Builder setOutputParameters(List<XTParameter> outputParameters) {
            this.outputParameters = outputParameters;
            return this;
        }

        public Builder addInputParameters(List<XTParameter> inputParameters) {
            if (inputParameters == null || inputParameters.isEmpty()) {
                return this;
            }

            if (this.inputParameters == null) {
                this.inputParameters = inputParameters;
            } else {
                this.inputParameters.addAll(inputParameters);
            }
            return this;
        }

        public Builder addInputParameters(XTParameter inputParameters) {
            if (inputParameters == null) {
                return this;
            }

            List<XTParameter> tmp = new ArrayList<>();
            tmp.add(inputParameters);
            return addInputParameters(tmp);
        }

        public Builder addOutputParameters(List<XTParameter> outputParameters) {
            if (outputParameters == null || outputParameters.isEmpty()) {
                return this;
            }

            if (this.outputParameters == null) {
                this.outputParameters = outputParameters;
            } else {
                this.outputParameters.addAll(outputParameters);
            }
            return this;
        }

        public Builder addOutputParameters(XTParameter outputParameters) {
            if (outputParameters == null) {
                return this;
            }

            List<XTParameter> tmp = new ArrayList<>();
            tmp.add(outputParameters);
            return addOutputParameters(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public XTOperation build() {
            return new XTOperation(this);
        }
    }
}

