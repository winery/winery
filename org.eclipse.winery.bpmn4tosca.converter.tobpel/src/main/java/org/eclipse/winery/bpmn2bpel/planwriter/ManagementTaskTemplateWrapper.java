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
package org.eclipse.winery.bpmn2bpel.planwriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.bpmn2bpel.model.ManagementTask;
import org.eclipse.winery.bpmn2bpel.model.param.Parameter;
import org.eclipse.winery.bpmn2bpel.model.param.TopologyParameter;


/**
 * Add convenience methods to the {@link ManagementTask} that can be used in the Velocity template.
 */
public class ManagementTaskTemplateWrapper extends ManagementTask {

	public ManagementTaskTemplateWrapper(ManagementTask task) {
		super();
		setId(task.getId());
		setName(task.getName());
		setType(task.getType());
		setInterfaceName(task.getInterfaceName());
		setNodeTemplateId(task.getNodeTemplateId());
		setNodeOperation(task.getNodeOperation());
		setInputParameters(task.getInputParameters());
		setOutputParameters(task.getOutputParameters());
	}

	public List<QName> getInputNodeTemplateIds() {
		// Velocity does just support java lists in templates but not sets;
		return new ArrayList<QName>(getNodeTemplateIds(getInputParameters()));
	}

	public List<QName> getOutputNodeTemplateIds() {
		// Velocity does just support java lists in templates but not sets;
		return new ArrayList<QName>(getNodeTemplateIds(getOutputParameters()));
	}

	/**
	 * @return The set union of all ids of node templates used by the task.
	 */
	public List<QName> getAllNodeTemplateIds() {
		Set<QName> allNodeTemplates = getNodeTemplateIds(getInputParameters());
		allNodeTemplates.addAll(getNodeTemplateIds(getOutputParameters()));
		return new ArrayList<QName>(allNodeTemplates);
	}

	/**
	 * Helper that returns a set of ids of the node templates used by the parameters.
	 *
	 * @return Set of node template ids as QNames
	 */
	private Set<QName> getNodeTemplateIds(List<Parameter> parameters) {
		Set<QName> nodeTemplateIds = new HashSet<QName>();

		Iterator<Parameter> iter = parameters.iterator();
		while (iter.hasNext()) {
			Parameter parameter = (Parameter) iter.next();
			/* Just topology parameter refer to node templates */
			if (parameter instanceof TopologyParameter) {
				nodeTemplateIds.add(((TopologyParameter) parameter).getNodeTemplateId());
			}
		}
		return nodeTemplateIds;
	}
}
