/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common.ids.admin;

import org.eclipse.winery.common.ids.IdNames;
import org.eclipse.winery.common.ids.XMLId;

public class ConstraintTypesId extends TypesId {

	private final static XMLId xmlId = new XMLId(IdNames.CONSTRAINTTYPES, false);


	public ConstraintTypesId() {
		super(ConstraintTypesId.xmlId);
	}

}
