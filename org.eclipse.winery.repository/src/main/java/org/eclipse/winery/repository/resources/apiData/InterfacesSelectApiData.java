/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 * Lukas Harzenetter - initial API and implementation
 */

package org.eclipse.winery.repository.resources.apiData;

import java.util.List;

import org.eclipse.winery.repository.datatypes.select2.Select2DataItem;

public class InterfacesSelectApiData extends Select2DataItem {
	public List<String> operations;

	public InterfacesSelectApiData(String text, List<String> operations) {
		super(text, text);
		this.operations = operations;
	}
}
