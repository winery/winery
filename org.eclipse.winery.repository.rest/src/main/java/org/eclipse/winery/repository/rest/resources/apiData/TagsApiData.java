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
package org.eclipse.winery.repository.rest.resources.apiData;

import org.eclipse.winery.model.tosca.TTag;

public class TagsApiData {
	public String id;
	public String name;
	public String value;

	public TagsApiData() {
	}

	public TagsApiData(String id, TTag data) {
		this.id = id;
		this.name = data.getName();
		this.value = data.getValue();
	}
}
