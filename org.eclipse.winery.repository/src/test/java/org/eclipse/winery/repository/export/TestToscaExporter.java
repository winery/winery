/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.export;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.repository.PrefsTestEnabledGitBackedRepository;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;

import org.apache.commons.io.output.NullOutputStream;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Working on old test repository")
public class TestToscaExporter {

	//private static final TOSCAExportUtil toscaExporter = new TOSCAExportUtil();
	private static final CSARExporter csarExporter = new CSARExporter();

	private static final ServiceTemplateId serviceTemplateId = new ServiceTemplateId("http://www.example.com/tosca/ServiceTemplates/Moodle", "Moodle", false);


	/**
	 * Quick hack as we currently don't have a dedicated test service template
	 */
	@BeforeClass
	public static void setServiceTemplateId() throws Exception {
		// Initialize preferences
		// We do not need them, but constructing them has the side effect that Repository.INSTANCE is != null
		new PrefsTestEnabledGitBackedRepository();
	}

	@Before
	public void setRevision() throws Exception {
		((GitBasedRepository) Repository.INSTANCE).setRevisionTo("97fa997b92965d8bc84e86274b0203f1db7495c5");
	}

	@Test
	public void checkTOSCAExport() throws Exception {
		@SuppressWarnings("unused")
		StreamingOutput so = output -> {
            TOSCAExportUtil exporter = new TOSCAExportUtil();
            // we include everything related
            Map<String, Object> conf = new HashMap<>();
            try {
                exporter.exportTOSCA(TestToscaExporter.serviceTemplateId, output, conf);
            } catch (JAXBException e) {
                throw new WebApplicationException(e);
            }
        };

		// TODO: check output contained in SO
	}

	@Test
	public void checkCSARExport() throws Exception {
		NullOutputStream out = new NullOutputStream();
		TestToscaExporter.csarExporter.writeCSAR(TestToscaExporter.serviceTemplateId, out);
	}
}
