/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml;

import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.IVisitor;
import org.eclipse.winery.model.tosca.yaml.visitor.VisitorNode;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tConstraintClause", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "equal",
    "greaterThan",
    "greaterOrEqual",
    "lessThan",
    "inRange",
    "validValues",
    "length",
    "minLength",
    "maxLength",
    "pattern"
})
public class TConstraintClause implements VisitorNode {
    private Object equal;
    @XmlAttribute(name = "greater_than")
    private Object greaterThan;
    @XmlAttribute(name = "greater_or_equal")
    private Object greaterOrEqual;
    @XmlAttribute(name = "less_than")
    private Object lessThan;
    @XmlAttribute(name = "less_or_equal")
    private Object lessOrEqual;
    @XmlAttribute(name = "in_range")
    private List<Object> inRange;
    @XmlAttribute(name = "valid_values")
    private List<Object> validValues;
    private Object length;
    @XmlAttribute(name = "min_length")
    private Object minLength;
    @XmlAttribute(name = "max_length")
    private Object maxLength;
    private Object pattern;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TConstraintClause)) return false;
        TConstraintClause that = (TConstraintClause) o;
        return Objects.equals(getEqual(), that.getEqual()) &&
            Objects.equals(getGreaterThan(), that.getGreaterThan()) &&
            Objects.equals(getGreaterOrEqual(), that.getGreaterOrEqual()) &&
            Objects.equals(getLessThan(), that.getLessThan()) &&
            Objects.equals(getLessOrEqual(), that.getLessOrEqual()) &&
            Objects.equals(getInRange(), that.getInRange()) &&
            Objects.equals(getValidValues(), that.getValidValues()) &&
            Objects.equals(getLength(), that.getLength()) &&
            Objects.equals(getMinLength(), that.getMinLength()) &&
            Objects.equals(getMaxLength(), that.getMaxLength()) &&
            Objects.equals(getPattern(), that.getPattern());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEqual(), getGreaterThan(), getGreaterOrEqual(), getLessThan(), getLessOrEqual(), getInRange(), getValidValues(), getLength(), getMinLength(), getMaxLength(), getPattern());
    }

    public Object getEqual() {
        return equal;
    }

    public void setEqual(Object equal) {
        this.equal = equal;
    }

    public Object getGreaterThan() {
        return greaterThan;
    }

    public void setGreaterThan(Object greaterThan) {
        this.greaterThan = greaterThan;
    }

    public Object getGreaterOrEqual() {
        return greaterOrEqual;
    }

    public void setGreaterOrEqual(Object greaterOrEqual) {
        this.greaterOrEqual = greaterOrEqual;
    }

    public Object getLessThan() {
        return lessThan;
    }

    public void setLessThan(Object lessThan) {
        this.lessThan = lessThan;
    }

    public Object getLessOrEqual() {
        return lessOrEqual;
    }

    public void setLessOrEqual(Object lessOrEqual) {
        this.lessOrEqual = lessOrEqual;
    }

    public List<Object> getInRange() {
        return inRange;
    }

    public void setInRange(List<Object> inRange) {
        this.inRange = inRange;
    }

    public List<Object> getValidValues() {
        return validValues;
    }

    public void setValidValues(List<Object> validValues) {
        this.validValues = validValues;
    }

    public Object getLength() {
        return length;
    }

    public void setLength(Object length) {
        this.length = length;
    }

    public Object getMinLength() {
        return minLength;
    }

    public void setMinLength(Object minLength) {
        this.minLength = minLength;
    }

    public Object getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Object maxLength) {
        this.maxLength = maxLength;
    }

    public Object getPattern() {
        return pattern;
    }

    public void setPattern(Object pattern) {
        this.pattern = pattern;
    }

    public <R extends AbstractResult<R>, P extends AbstractParameter<P>> R accept(IVisitor<R, P> visitor, P parameter) {
        return visitor.visit(this, parameter);
    }
}
