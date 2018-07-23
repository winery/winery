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

import org.eclipse.winery.compliance.model.ToscaEdge;
import org.eclipse.winery.compliance.model.ToscaNode;

public class ToscaComparatorProxy {
	private final IToscaMatcher matcher;

	public ToscaComparatorProxy(IToscaMatcher matcher) {
		this.matcher = (matcher != null) ? matcher : new ToscaDefaultMatcher();
	}

	public int compareTypeCompatible(ToscaNode left, ToscaNode right) {
		return matcher.isCompatible(left, right) ? 0 : -1;
	}

	public int compareTypeCompatible(ToscaEdge left, ToscaEdge right) {
		return matcher.isCompatible(left, right) ? 0 : -1;
	}
}
