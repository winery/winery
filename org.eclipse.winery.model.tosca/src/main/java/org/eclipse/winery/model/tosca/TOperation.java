/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial code generation using vhudson-jaxb-ri-2.1-2
 *******************************************************************************/

package org.eclipse.winery.model.tosca;

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

import org.eclipse.jdt.annotation.NonNull;


/**
 * <p>Java class for tOperation complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tOperation">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/tosca/ns/2011/12}tExtensibleElements">
 *       &lt;sequence>
 *         &lt;element name="InputParameters" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="InputParameter" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tParameter"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="OutputParameters" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="OutputParameter" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tParameter"
 * maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
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

    /**
     * Gets the value of the inputParameters property.
     *
     * @return possible object is {@link TOperation.InputParameters }
     */
    /*@Nullable*/
    public TOperation.InputParameters getInputParameters() {
        return inputParameters;
    }

    /**
     * Sets the value of the inputParameters property.
     *
     * @param value allowed object is {@link TOperation.InputParameters }
     */
    public void setInputParameters(TOperation.InputParameters value) {
        this.inputParameters = value;
    }

    /**
     * Gets the value of the outputParameters property.
     *
     * @return possible object is {@link TOperation.OutputParameters }
     */
    /*@Nullable*/
    public TOperation.OutputParameters getOutputParameters() {
        return outputParameters;
    }

    /**
     * Sets the value of the outputParameters property.
     *
     * @param value allowed object is {@link TOperation.OutputParameters }
     */
    public void setOutputParameters(TOperation.OutputParameters value) {
        this.outputParameters = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="InputParameter" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tParameter"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "inputParameter"
    })
    public static class InputParameters {

        @XmlElement(name = "InputParameter", required = true)
        protected List<TParameter> inputParameter;

        /**
         * Gets the value of the inputParameter property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the inputParameter property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getInputParameter().add(newItem);
         * </pre>
         *
         *
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
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="OutputParameter" type="{http://docs.oasis-open.org/tosca/ns/2011/12}tParameter"
     * maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "outputParameter"
    })
    public static class OutputParameters {

        @XmlElement(name = "OutputParameter", required = true)
        protected List<TParameter> outputParameter;

        /**
         * Gets the value of the outputParameter property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the outputParameter property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOutputParameter().add(newItem);
         * </pre>
         *
         *
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

