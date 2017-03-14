/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Sebastian Wagner - initial API and implementation
 *     ZTE - support of more gateways
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Gateway extends Node {
	private List<GatewayBranch> branchList = new ArrayList<GatewayBranch>();

	public List<GatewayBranch> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<GatewayBranch> branches) {
		this.branchList = branches;
	}

	public Optional<GatewayBranch> getBranch(String id) {
		for (GatewayBranch branch : branchList) {
			if (id.equals(branch.getId())) {
				return Optional.of(branch);
			}
		}

		return Optional.empty();
	}

}
