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

package org.eclipse.winery.repository.rest.resources.apiData.boundarydefinitions;

import org.eclipse.winery.model.tosca.TPropertyConstraint;

public class PropertyConstraintsApiData {
	String property;
	Object fragments;
	String constraintType;


	public PropertyConstraintsApiData() { }

	public PropertyConstraintsApiData(TPropertyConstraint propertyConstraint) {
		this.setProperty(propertyConstraint.getProperty());
		this.setFragments(propertyConstraint.getAny());
		this.setConstraintType(propertyConstraint.getConstraintType());
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getConstraintType() {
		return constraintType;
	}

	public void setConstraintType(String constraintType) {
		this.constraintType = constraintType;
	}

	public Object getFragments() {
		return fragments;
	}

	public void setFragments(Object fragments) {
		this.fragments = fragments;
	}

}
