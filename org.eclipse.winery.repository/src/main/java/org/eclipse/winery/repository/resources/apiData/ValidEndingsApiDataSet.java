/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Nicole Keppler - initial API and implementation
 */
package org.eclipse.winery.repository.resources.apiData;

import org.eclipse.winery.repository.datatypes.select2.Select2DataItem;

enum ValidEndingsTypeEnum {
	EVERYTHING, NODETYPE, REQTYPE, CAPTYPE;
}

public class ValidEndingsApiDataSet {
	public ValidEndingsTypeEnum validEndingsSelectionType;
	public Select2DataItem validDataSet;

	public ValidEndingsApiDataSet() {
	}

	public ValidEndingsApiDataSet(String type, Select2DataItem validDataSet) {
		switch (type) {
			case "everything":
				this.validEndingsSelectionType = ValidEndingsTypeEnum.EVERYTHING;
				break;
			case "nodeType":
				this.validEndingsSelectionType = ValidEndingsTypeEnum.NODETYPE;
				break;
			case "reqType":
				this.validEndingsSelectionType = ValidEndingsTypeEnum.REQTYPE;
				break;
			case "capType":
				this.validEndingsSelectionType = ValidEndingsTypeEnum.CAPTYPE;
				break;
			default:
				this.validEndingsSelectionType = ValidEndingsTypeEnum.EVERYTHING;
				break;
		}
		this.validDataSet = validDataSet;
	}
}
