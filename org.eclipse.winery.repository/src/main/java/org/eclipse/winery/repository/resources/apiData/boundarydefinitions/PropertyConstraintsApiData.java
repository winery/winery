/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.repository.resources.apiData.boundarydefinitions;

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
