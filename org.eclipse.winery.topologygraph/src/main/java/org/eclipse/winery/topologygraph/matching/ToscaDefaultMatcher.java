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
package org.eclipse.winery.topologygraph.matching;

import org.eclipse.winery.topologygraph.matching.model.ToscaEdge;
import org.eclipse.winery.topologygraph.matching.model.ToscaNode;

import org.apache.commons.lang3.StringUtils;

public class ToscaDefaultMatcher implements IToscaMatcher {

	@Override
	public boolean isCompatible(ToscaNode left, ToscaNode right) {
		return StringUtils.equals(left.getNodeTemplate().getName(), right.getNodeTemplate().getName());
	}

	@Override
	public boolean isCompatible(ToscaEdge left, ToscaEdge right) {
		return StringUtils.equals(left.getTemplate().getName(), right.getTemplate().getName());
	}
}
