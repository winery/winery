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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tOperation", propOrder = {
    "inputParameters",
    "outputParameters"
})
public class TOperation extends TExtensibleElements {
    @XmlElement(name = "InputParameters")
    protected TOperation.InputParameters inputParameters;
    @XmlElement(name = "OutputParameters")
    protected TOperation.OutputParameters outputParameters;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

    public TOperation() {
    }

    public TOperation(Builder builder) {
        super(builder);
        this.inputParameters = builder.inputParameters;
        this.outputParameters = builder.outputParameters;
        this.name = builder.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TOperation)) return false;
        if (!super.equals(o)) return false;
        TOperation that = (TOperation) o;
        return Objects.equals(inputParameters, that.inputParameters) &&
            Objects.equals(outputParameters, that.outputParameters) &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), inputParameters, outputParameters, name);
    }

    public TOperation.@Nullable InputParameters getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(TOperation.@Nullable InputParameters value) {
        this.inputParameters = value;
    }

    public TOperation.@Nullable OutputParameters getOutputParameters() {
        return outputParameters;
    }

    public void setOutputParameters(TOperation.@Nullable OutputParameters value) {
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

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "inputParameter"
    })
    public static class InputParameters implements Serializable {

        @XmlElement(name = "InputParameter", required = true)
        protected List<TParameter> inputParameter;

        @NonNull
        public List<TParameter> getInputParameter() {
            if (inputParameter == null) {
                inputParameter = new ArrayList<TParameter>();
            }
            return this.inputParameter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InputParameters that = (InputParameters) o;
            return Objects.equals(inputParameter, that.inputParameter);
        }

        @Override
        public int hashCode() {
            return Objects.hash(inputParameter);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "outputParameter"
    })
    public static class OutputParameters implements Serializable {

        @XmlElement(name = "OutputParameter", required = true)
        protected List<TParameter> outputParameter;

        @NonNull
        public List<TParameter> getOutputParameter() {
            if (outputParameter == null) {
                outputParameter = new ArrayList<TParameter>();
            }
            return this.outputParameter;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OutputParameters that = (OutputParameters) o;
            return Objects.equals(outputParameter, that.outputParameter);
        }

        @Override
        public int hashCode() {
            return Objects.hash(outputParameter);
        }
    }

    public static class Builder extends TExtensibleElements.Builder<Builder> {
        private final String name;
        private InputParameters inputParameters;
        private OutputParameters outputParameters;

        public Builder(String name) {
            this.name = name;
        }

        public Builder setInputParameters(TOperation.InputParameters inputParameters) {
            this.inputParameters = inputParameters;
            return this;
        }

        public Builder setOutputParameters(TOperation.OutputParameters outputParameters) {
            this.outputParameters = outputParameters;
            return this;
        }

        public Builder addInputParameters(TOperation.InputParameters inputParameters) {
            if (inputParameters == null || inputParameters.getInputParameter().isEmpty()) {
                return this;
            }

            if (this.inputParameters == null) {
                this.inputParameters = inputParameters;
            } else {
                this.inputParameters.getInputParameter().addAll(inputParameters.getInputParameter());
            }
            return this;
        }

        public Builder addInputParameters(List<TParameter> inputParameters) {
            if (inputParameters == null) {
                return this;
            }

            TOperation.InputParameters tmp = new TOperation.InputParameters();
            tmp.getInputParameter().addAll(inputParameters);
            return addInputParameters(tmp);
        }

        public Builder addInputParameters(TParameter inputParameters) {
            if (inputParameters == null) {
                return this;
            }

            TOperation.InputParameters tmp = new TOperation.InputParameters();
            tmp.getInputParameter().add(inputParameters);
            return addInputParameters(tmp);
        }

        public Builder addOutputParameters(TOperation.OutputParameters outputParameters) {
            if (outputParameters == null || outputParameters.getOutputParameter().isEmpty()) {
                return this;
            }

            if (this.outputParameters == null) {
                this.outputParameters = outputParameters;
            } else {
                this.outputParameters.getOutputParameter().addAll(outputParameters.getOutputParameter());
            }
            return this;
        }

        public Builder addOutputParameters(List<TParameter> outputParameters) {
            if (outputParameters == null) {
                return this;
            }

            TOperation.OutputParameters tmp = new TOperation.OutputParameters();
            tmp.getOutputParameter().addAll(outputParameters);
            return addOutputParameters(tmp);
        }

        public Builder addOutputParameters(TParameter outputParameters) {
            if (outputParameters == null) {
                return this;
            }

            TOperation.OutputParameters tmp = new TOperation.OutputParameters();
            tmp.getOutputParameter().add(outputParameters);
            return addOutputParameters(tmp);
        }

        @Override
        public Builder self() {
            return this;
        }

        public TOperation build() {
            return new TOperation(this);
        }
    }
}

