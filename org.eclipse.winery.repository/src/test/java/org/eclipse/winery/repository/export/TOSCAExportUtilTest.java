/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.winery.repository.export;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.output.NullOutputStream;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("not finished - CSARExporterTest is currently the way to go")
public class TOSCAExportUtilTest {

	@Test
	public void exportTOSCA() throws Exception {
		TOSCAExportUtil exporter = new TOSCAExportUtil();
		// we include everything related
		Map<String, Object> conf = new HashMap<>();
		NullOutputStream out = new NullOutputStream();
		// exporter.exportTOSCA(TestToscaExporter.serviceTemplateId, out, conf);
	}

}
