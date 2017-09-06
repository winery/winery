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
 *     Armin Hüneburg - fixed path handling
 *     ZTE - support of more gateways
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel.planwriter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.bpmn2bpel.model.Gateway;
import org.eclipse.winery.bpmn2bpel.model.Link;
import org.eclipse.winery.bpmn2bpel.model.ManagementFlow;
import org.eclipse.winery.bpmn2bpel.model.ManagementTask;
import org.eclipse.winery.bpmn2bpel.model.Node;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BpelPlanArtefactWriter {

	public static String TEMPLATE_PATH = "./src/main/resources/templates/";

	private static final Logger LOGGER = LoggerFactory.getLogger(BpelPlanArtefactWriter.class);

	private ManagementFlow mangagementFlow;

	public BpelPlanArtefactWriter(ManagementFlow mangagementFlow) {
		this.mangagementFlow = mangagementFlow;
		Velocity.init();
	}

	public String completePlanTemplate() {
		LOGGER.debug("Completing BPEL process template...");

		/* Traverse  the management flow and add the nodes in the order of their execution to a list */
		List<Node> managementTaskSeq = new ArrayList<Node>();
		GraphIterator<Node, Link> iterator = new DepthFirstIterator<Node, Link>(mangagementFlow);
		while (iterator.hasNext()) {
			Node node = iterator.next();
			/* In this version the templates do only support management tasks and exclusive gateway */
			if (node instanceof ManagementTask) {
				/* Wrapper adds convenience functions that can be accessed from the Velocity template */
				ManagementTaskTemplateWrapper taskWrapper = new ManagementTaskTemplateWrapper((ManagementTask) node); //TODO move to factory and remove setters from constructor
				managementTaskSeq.add(taskWrapper);
			} else if (node instanceof Gateway) {
				managementTaskSeq.add(node);
			}
		}

		VelocityContext context = new VelocityContext();
		/* In the Velocity template for each management task an own scope is created containing the variables and
		 * activities required to perform the management task based on the properties of the respective task  */
		Template planTemplate = Velocity.getTemplate(TEMPLATE_PATH + "bpel_management_plan_template.xml");
		context.put("mngmtTaskList", managementTaskSeq);
		StringWriter planWriter = new StringWriter();
		planTemplate.merge( context, planWriter );

		String bpelProcessContent = planWriter.toString();

		LOGGER.debug("Completed BPEL process template" + bpelProcessContent);

		return bpelProcessContent;

	}

	public String completePlanWsdlTemplate() {
		LOGGER.debug("Completing BPEL WSDL template");

		VelocityContext context = new VelocityContext();
		Template wsdlTemplate = Velocity.getTemplate(TEMPLATE_PATH + "management_plan_wsdl_template.xml");

		StringWriter wsdlWriter = new StringWriter();
		wsdlTemplate.merge( context, wsdlWriter );

		String bpelProcessWSDL = wsdlWriter.toString();

		LOGGER.debug("Completed BPEL WSDL template" + bpelProcessWSDL);

		return bpelProcessWSDL;
	}

	public String completeInvokerWsdlTemplate() {
		LOGGER.debug("Retrieving service invoker WSDL");

		VelocityContext context = new VelocityContext();
		Template invokerWsdlTemplate = Velocity.getTemplate(TEMPLATE_PATH + "invoker.wsdl");

		StringWriter wsdlWriter = new StringWriter();
		invokerWsdlTemplate.merge( context, wsdlWriter );

		return wsdlWriter.toString();
	}

	public String completeInvokerXsdTemplate() {
		LOGGER.debug("Retrieving service invoker XSD");

		VelocityContext context = new VelocityContext();
		Template invokerXsdTemplate = Velocity.getTemplate(TEMPLATE_PATH + "invoker.xsd");

		StringWriter xsdWriter = new StringWriter();
		invokerXsdTemplate.merge( context, xsdWriter );

		return xsdWriter.toString();
	}

	public String completeDeploymentDescriptorTemplate() {
		LOGGER.debug("Retrieving Apache ODE deployment descriptor");

		VelocityContext context = new VelocityContext();
		Template invokerXsdTemplate = Velocity.getTemplate(TEMPLATE_PATH + "deploy.xml");

		StringWriter xsdWriter = new StringWriter();
		invokerXsdTemplate.merge( context, xsdWriter );

		return xsdWriter.toString();
	}

}
