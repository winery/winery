/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */

package org.eclipse.winery.repository.rest.resources.apiData;

import org.eclipse.winery.repository.rest.resources.AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal;

public class InheritanceResourceApiData {

	public String isAbstract;
	public String isFinal;
	public String derivedFrom;

	public InheritanceResourceApiData() {
	}

	public InheritanceResourceApiData(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal res) {
		this.isAbstract = res.getTBoolean("getAbstract");
		this.isFinal = res.getTBoolean("getFinal");
		this.derivedFrom = res.getDerivedFrom() == null ? "(none)" : res.getDerivedFrom();
	}

	public String toString() {
		return "InheritanceResourceJson: { isAbstract: " + this.isAbstract + ", isFinal: " + this.isFinal + ", derivedFrom: " + this.derivedFrom + " }";
	}
}
