/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.winery.model.tosca.visitor.Visitor;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.w3c.dom.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPlan", propOrder = {
    "precondition",
    "inputParameters",
    "outputParameters",
    "planModel",
    "planModelReference"
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class TPlan extends TExtensibleElements {
    @XmlElement(name = "Precondition")
    protected TCondition precondition;
    @XmlElement(name = "InputParameters")
    protected TPlan.InputParameters inputParameters;
    @XmlElement(name = "OutputParameters")
    protected TPlan.OutputParameters outputParameters;
    @XmlElement(name = "PlanModel")
    protected TPlan.PlanModel planModel;
    @XmlElement(name = "PlanModelReference")
    protected TPlan.PlanModelReference planModelReference;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "planType", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String planType;
    @XmlAttribute(name = "planLanguage", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String planLanguage;

    @Deprecated // used for XML deserialization of API request content
    public TPlan() { }

    public TPlan(Builder builder) {
        super(builder);
        this.precondition = builder.precondition;
        this.inputParameters = builder.inputParameters;
        this.outputParameters = builder.outputParameters;
        this.planModel = builder.planModel;
        this.planModelReference = builder.planModelReference;
        this.id = builder.id;
        this.name = builder.name;
        this.planType = builder.planType;
        this.planLanguage = builder.planLanguage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TPlan)) return false;
        if (!super.equals(o)) return false;
        TPlan tPlan = (TPlan) o;
        return Objects.equals(precondition, tPlan.precondition) &&
            Objects.equals(inputParameters, tPlan.inputParameters) &&
            Objects.equals(outputParameters, tPlan.outputParameters) &&
            Objects.equals(planModel, tPlan.planModel) &&
            Objects.equals(planModelReference, tPlan.planModelReference) &&
            Objects.equals(id, tPlan.id) &&
            Objects.equals(name, tPlan.name) &&
            Objects.equals(planType, tPlan.planType) &&
            Objects.equals(planLanguage, tPlan.planLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), precondition, inputParameters, outputParameters, planModel, planModelReference, id, name, planType, planLanguage);
    }

    @Nullable
    public TCondition getPrecondition() {
        return precondition;
    }

    public void setPrecondition(@Nullable TCondition value) {
        this.precondition = value;
    }

    public TPlan.@Nullable InputParameters getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(TPlan.@Nullable InputParameters value) {
        this.inputParameters = value;
    }

    public TPlan.@Nullable OutputParameters getOutputParameters() {
        return outputParameters;
    }

    public void setOutputParameters(TPlan.@Nullable OutputParameters value) {
        this.outputParameters = value;
    }

    public TPlan.@Nullable PlanModel getPlanModel() {
        return planModel;
    }

    public void setPlanModel(TPlan.@Nullable PlanModel value) {
        this.planModel = value;
    }

    public TPlan.@Nullable PlanModelReference getPlanModelReference() {
        return planModelReference;
    }

    public void setPlanModelReference(TPlan.@Nullable PlanModelReference value) {
        this.planModelReference = value;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String value) {
        this.id = value;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String value) {
        this.name = value;
    }

    @NonNull
    public String getPlanType() {
        return planType;
    }

    public void setPlanType(@NonNull String value) {
        this.planType = Objects.requireNonNull(value);
    }

    @NonNull
    public String getPlanLanguage() {
        return planLanguage;
    }

    public void setPlanLanguage(@NonNull String value) {
        this.planLanguage = Objects.requireNonNull(value);
    }

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

        /**
         * Gets the value of the inputParameter property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the inputParameter property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInputParameter().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TParameter }
         */
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

        /**
         * Gets the value of the outputParameter property.
         * <p>
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the outputParameter property.
         * <p>
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOutputParameter().add(newItem);
         * </pre>
         * <p>
         * <p>
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TParameter }
         */
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

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class PlanModel implements Serializable {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Gets the value of the any property.
         *
         * @return possible object is {@link Element } {@link Object }
         */
        @Nullable
        public Object getAny() {
            return any;
        }

        /**
         * Sets the value of the any property.
         *
         * @param value allowed object is {@link Element } {@link Object }
         */
        public void setAny(Object value) {
            this.any = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlanModel planModel = (PlanModel) o;
            return Objects.equals(any, planModel.any);
        }

        @Override
        public int hashCode() {

            return Objects.hash(any);
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PlanModelReference implements Serializable {

        @XmlAttribute(name = "reference", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String reference;

        /**
         * Gets the value of the reference property.
         *
         * @return possible object is {@link String }
         */
        @NonNull
        public String getReference() {
            return reference;
        }

        /**
         * Sets the value of the reference property.
         *
         * @param value allowed object is {@link String }
         */
        public void setReference(String value) {
            this.reference = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlanModelReference that = (PlanModelReference) o;
            return Objects.equals(reference, that.reference);
        }

        @Override
        public int hashCode() {
            return Objects.hash(reference);
        }
    }

    public static class Builder extends TExtensibleElements.Builder<Builder> {
        private final String id;
        private final String planType;
        private final String planLanguage;

        private TCondition precondition;
        private InputParameters inputParameters;
        private OutputParameters outputParameters;
        private PlanModel planModel;
        private PlanModelReference planModelReference;
        private String name;

        public Builder(String id, String planType, String planLanguage) {
            this.id = id;
            this.planType = planType;
            this.planLanguage = planLanguage;
        }

        public Builder setPrecondition(TCondition precondition) {
            this.precondition = precondition;
            return this;
        }

        public Builder setInputParameters(InputParameters inputParameters) {
            this.inputParameters = inputParameters;
            return this;
        }

        public Builder setOutputParameters(OutputParameters outputParameters) {
            this.outputParameters = outputParameters;
            return this;
        }

        public Builder setPlanModel(PlanModel planModel) {
            this.planModel = planModel;
            return this;
        }

        public Builder setPlanModelReference(PlanModelReference planModelReference) {
            this.planModelReference = planModelReference;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        public TPlan build() {
            return new TPlan(this);
        }
    }
}
