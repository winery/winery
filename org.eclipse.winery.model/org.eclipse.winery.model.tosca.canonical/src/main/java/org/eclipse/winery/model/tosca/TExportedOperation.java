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
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TExportedOperation implements HasName, Serializable {

    protected TExportedOperation.NodeOperation nodeOperation;
    protected TExportedOperation.RelationshipOperation relationshipOperation;
    protected TExportedOperation.Plan plan;
    protected String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TExportedOperation)) return false;
        TExportedOperation that = (TExportedOperation) o;
        return Objects.equals(nodeOperation, that.nodeOperation) &&
            Objects.equals(relationshipOperation, that.relationshipOperation) &&
            Objects.equals(plan, that.plan) &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeOperation, relationshipOperation, plan, name);
    }

    /**
     * Gets the value of the nodeOperation property.
     *
     * @return possible object is {@link TExportedOperation.NodeOperation }
     */
    public TExportedOperation.@Nullable NodeOperation getNodeOperation() {
        return nodeOperation;
    }

    /**
     * Sets the value of the nodeOperation property.
     *
     * @param value allowed object is {@link TExportedOperation.NodeOperation }
     */
    public void setNodeOperation(TExportedOperation.NodeOperation value) {
        this.nodeOperation = value;
    }

    /**
     * Gets the value of the relationshipOperation property.
     *
     * @return possible object is {@link TExportedOperation.RelationshipOperation }
     */
    public TExportedOperation.@Nullable RelationshipOperation getRelationshipOperation() {
        return relationshipOperation;
    }

    /**
     * Sets the value of the relationshipOperation property.
     *
     * @param value allowed object is {@link TExportedOperation.RelationshipOperation }
     */
    public void setRelationshipOperation(TExportedOperation.RelationshipOperation value) {
        this.relationshipOperation = value;
    }

    /**
     * Gets the value of the plan property.
     *
     * @return possible object is {@link TExportedOperation.Plan }
     */
    public TExportedOperation.@Nullable Plan getPlan() {
        return plan;
    }

    /**
     * Sets the value of the plan property.
     *
     * @param value allowed object is {@link TExportedOperation.Plan }
     */
    public void setPlan(TExportedOperation.Plan value) {
        this.plan = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    @NonNull
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     */
    @Override
    public void setName(String value) {
        this.name = value;
    }

    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="nodeRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
     *       &lt;attribute name="interfaceName" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *       &lt;attribute name="operationName" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NodeOperation implements Serializable {

        @JsonIdentityReference(alwaysAsId = true)
        protected Object nodeRef;

        protected String interfaceName;

        protected String operationName;

        /**
         * Gets the value of the nodeRef property.
         *
         * @return possible object is {@link Object }
         */
        @NonNull
        public Object getNodeRef() {
            return nodeRef;
        }

        /**
         * Sets the value of the nodeRef property.
         *
         * @param value allowed object is {@link Object }
         */
        public void setNodeRef(Object value) {
            this.nodeRef = value;
        }

        /**
         * Gets the value of the interfaceName property.
         *
         * @return possible object is {@link String }
         */
        @NonNull
        public String getInterfaceName() {
            return interfaceName;
        }

        /**
         * Sets the value of the interfaceName property.
         *
         * @param value allowed object is {@link String }
         */
        public void setInterfaceName(String value) {
            this.interfaceName = value;
        }

        /**
         * Gets the value of the operationName property.
         *
         * @return possible object is {@link String }
         */
        @NonNull
        public String getOperationName() {
            return operationName;
        }

        /**
         * Sets the value of the operationName property.
         *
         * @param value allowed object is {@link String }
         */
        public void setOperationName(String value) {
            this.operationName = value;
        }
    }

    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="planRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Plan implements Serializable {

        @JsonIdentityReference(alwaysAsId = true)
        protected Object planRef;

        /**
         * Gets the value of the planRef property.
         *
         * @return possible object is {@link Object }
         */
        @NonNull
        public Object getPlanRef() {
            return planRef;
        }

        /**
         * Sets the value of the planRef property.
         *
         * @param value allowed object is {@link Object }
         */
        public void setPlanRef(Object value) {
            this.planRef = value;
        }
    }

    /**
     * <p>Java class for anonymous complex type.
     * <p>
     * <p>The following schema fragment specifies the expected content contained within this class.
     * <p>
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="relationshipRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
     *       &lt;attribute name="interfaceName" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
     *       &lt;attribute name="operationName" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RelationshipOperation implements Serializable {

        @JsonIdentityReference(alwaysAsId = true)
        protected Object relationshipRef;

        protected String interfaceName;

        protected String operationName;

        /**
         * Gets the value of the relationshipRef property.
         *
         * @return possible object is {@link Object }
         */
        @NonNull
        public Object getRelationshipRef() {
            return relationshipRef;
        }

        /**
         * Sets the value of the relationshipRef property.
         *
         * @param value allowed object is {@link Object }
         */
        public void setRelationshipRef(Object value) {
            this.relationshipRef = value;
        }

        /**
         * Gets the value of the interfaceName property.
         *
         * @return possible object is {@link String }
         */
        @NonNull
        public String getInterfaceName() {
            return interfaceName;
        }

        /**
         * Sets the value of the interfaceName property.
         *
         * @param value allowed object is {@link String }
         */
        public void setInterfaceName(String value) {
            this.interfaceName = value;
        }

        /**
         * Gets the value of the operationName property.
         *
         * @return possible object is {@link String }
         */
        @NonNull
        public String getOperationName() {
            return operationName;
        }

        /**
         * Sets the value of the operationName property.
         *
         * @param value allowed object is {@link String }
         */
        public void setOperationName(String value) {
            this.operationName = value;
        }
    }
}
