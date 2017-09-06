/*******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Sebastian Wagner - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.bpmn2bpel;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.bpmn2bpel.model.ManagementFlow;
import org.eclipse.winery.bpmn2bpel.parser.Bpmn4JsonParser;
import org.eclipse.winery.bpmn2bpel.parser.ParseException;
import org.eclipse.winery.bpmn2bpel.planwriter.BpelPlanArtefactWriter;
import org.eclipse.winery.bpmn2bpel.planwriter.PlanWriterException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bpmn4Tosca2Bpel {

	public static final String FILE_NAME_PLAN = "management_plan.bpel";

	public static final String FILE_NAME_PLAN_WSDL = "management_plan.wsdl";

	public static final String FILE_NAME_INVOKER_WSDL = "invoker.wsdl";

	public static final String FILE_NAME_INVOKER_XSD = "invoker.xsd";

	public static final String FILE_NAME_DEPLOYMENT_DESC = "deploy.xml";

	public static final String DIR_NAME_TEMP_BPMN4TOSCA = "bpmn4tosca";

	private static final Logger LOGGER = LoggerFactory.getLogger(Bpmn4Tosca2Bpel.class);

	/**
	 * Transforms the given BPMN4Tosca Json management into a BPEL management plan that can be enacted with the OpenTosca runtime.
	 * <p>
	 * The created zip file contains the following files
	 *
	 * - bpel plan
	 * - plan wsdl
	 * - sevice invoker wsdl
	 * - service invoker xsd
	 * - deployment descriptor
	 * generated BPEL plan, the corresponding WSDL files and a deployment descriptor.
	 *
	 *
	 * @param srcBpmn4ToscaJsonFile
	 * @param targetBPELArchive
	 *
	 * @throws ParseException
	 * @throws PlanWriterException
	 */
	public void transform(URI srcBpmn4ToscaJsonFile, URI targetBPELArchive) throws ParseException, PlanWriterException {
		//LOGGER.debug("Transforming ");

		Bpmn4JsonParser parser = new Bpmn4JsonParser();//TODO To singleton
		/* Create object representation of Json */
		ManagementFlow managementFlow = parser.parse(srcBpmn4ToscaJsonFile);

		List<Path> planArtefactPaths = new ArrayList<Path>();
		try {
			Path tempPath = FileUtil.createTempDir(DIR_NAME_TEMP_BPMN4TOSCA);

			BpelPlanArtefactWriter writer = new BpelPlanArtefactWriter(managementFlow);

			String plan = writer.completePlanTemplate();
			planArtefactPaths.add(FileUtil.writeStringToFile(plan, Paths.get(tempPath.toString(), FILE_NAME_PLAN)));

			String planWsdl = writer.completePlanWsdlTemplate();
			planArtefactPaths.add(FileUtil.writeStringToFile(planWsdl, Paths.get(tempPath.toString(), FILE_NAME_PLAN_WSDL)));

			String invokerWsdl = writer.completeInvokerWsdlTemplate();
			planArtefactPaths.add(FileUtil.writeStringToFile(invokerWsdl, Paths.get(tempPath.toString(), FILE_NAME_INVOKER_WSDL)));

			String invokerXsd = writer.completeInvokerXsdTemplate();
			planArtefactPaths.add(FileUtil.writeStringToFile(invokerXsd, Paths.get(tempPath.toString(), FILE_NAME_INVOKER_XSD)));

			String deploymentDesc = writer.completeDeploymentDescriptorTemplate();
			planArtefactPaths.add(FileUtil.writeStringToFile(deploymentDesc, Paths.get(tempPath.toString(), FILE_NAME_DEPLOYMENT_DESC)));

			LOGGER.debug("Creating BPEL Archive...");
			Path zipFilePath = FileUtil.createApacheOdeProcessArchive(Paths.get(targetBPELArchive), planArtefactPaths);
			LOGGER.debug("Management plan zip file saved to " + zipFilePath.toString());
		} catch (Exception e) {
			throw new PlanWriterException(e);
		} finally {
			/* Delete created plan artifact files from temp directory */
			try {
				LOGGER.debug("Deleting temporary plan artefact files...");
				FileUtil.deleteFiles(planArtefactPaths);
			} catch (IOException e) {
				throw new PlanWriterException(e);
			}
		}
	}

}
