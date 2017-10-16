/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.yaml;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.converter.yaml.support.AbstractTestY2X;

import org.junit.Assert;
import org.junit.Test;

public class Datatypes extends AbstractTestY2X {
	public Datatypes() {
		super("src/test/resources/yaml/Datatypes/");
	}

	@Test
	public void testDataTypes() throws Exception {
		String name = "data_types";
		String namespace = "http://www.example.com/DataTypesTest";

		TServiceTemplate serviceTemplate = readServiceTemplate(name, namespace);
		Definitions definitions = convert(serviceTemplate, name, namespace);
		writeXml(definitions, name, namespace);

		Assert.assertNotNull(definitions);
	}

	@Test
	public void testDataTypesWithImport() throws Exception {
		String name = "data_types-with_import";
		String namespace = "http://www.example.com/DataTypesWithImportTest";

		TServiceTemplate serviceTemplate = readServiceTemplate(name, namespace);
		Definitions definitions = convert(serviceTemplate, name, namespace);
		writeXml(definitions, name, namespace);
		
		Assert.assertNotNull(definitions);
	}

	@Test
	public void testDataTypesRecursive() throws Exception {
		String name = "data_types-recursive";
		String namespace = "http://www.example.com/DataTypesRecursive";

		TServiceTemplate serviceTemplate = readServiceTemplate(name, namespace);
		Definitions definitions = convert(serviceTemplate, name, namespace);
		writeXml(definitions, name, namespace);
		
		Assert.assertNotNull(definitions);
	}

	@Test
	public void testNodeTemplateWithDataTypes() throws Exception {
		String name = "node_template-using-data_types";
		String namespace = "http://www.example.com/NodeTemplateUsingDataType";

		TServiceTemplate serviceTemplate = readServiceTemplate(name, namespace);
		Definitions definitions = convert(serviceTemplate, name, namespace);
		writeXml(definitions, name, namespace);
		
		Assert.assertNotNull(definitions);
	}
}
