/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
package org.eclipse.winery.compliance.matching;

import org.eclipse.winery.compliance.model.TOSCAEdge;
import org.eclipse.winery.compliance.model.TOSCANode;

public class TOSCAComparatorProxy {
	ITOSCAMatcher matcher;

	public TOSCAComparatorProxy(ITOSCAMatcher matcher) {
		this.matcher = (matcher != null) ? matcher : new TOSCADefaultMatcher();
	}

	public int compareTypeCompatible(TOSCANode left, TOSCANode right) {
		return matcher.isCompatible(left, right) ? 0 : -1;
	}

	public int compareTypeCompatible(TOSCAEdge left, TOSCAEdge right) {
		return matcher.isCompatible(left, right) ? 0 : -1;
	}
}
