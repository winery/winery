/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.writer.WriterUtils;
import org.eclipse.winery.yaml.converter.Converter;
import org.eclipse.winery.yaml.converter.yaml.support.AbstractTestY2X;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class Showcases extends AbstractTestY2X {

	public Showcases() {
		super("src/test/resources/yaml/Showcase");
	}

	public MultiException convert(String path, String namespace, Stream<String> files) throws Exception {
		MultiException exception = new MultiException();

		files
			.map(name -> {
				try {
					return new LinkedHashMap.SimpleEntry<>(name, readServiceTemplate(path + File.separator + name));
				} catch (Exception e) {
					exception.add(e);
				}
				return null;
			})
			.filter(Objects::nonNull)
			.map(entry -> new LinkedHashMap.SimpleEntry<>(entry.getKey(), convert(entry.getValue(), entry.getKey(), namespace)))
			.forEach(entry -> WriterUtils.saveDefinitions(entry.getValue(), OUT_PATH, namespace, entry.getKey()));
		if (exception.hasException()) {
			throw exception.getException();
		}

		return exception;
	}

	@Ignore
	@Test
	public void nodeTypesTest() throws Exception {
		String path = "nodetypes";
		String namespace = "http://placeholder.org/nodetypes";
		Stream<String> files = Stream.of(
		);

		Assert.assertTrue(convert(path, namespace, files).isEmpty());
	}

	@Ignore
	@Test
	public void serviceTemplateTest() throws Exception {
		String path = "servicetemplates";
		String namespace = "http://placeholder.org/servicetemplates";
		Stream<String> files = Stream.of(
		);

		Assert.assertTrue(convert(path, namespace, files).isEmpty());
	}

	@Test
	public void zipTypeTest() throws Exception {
		String name = "Showcase.csar";
		InputStream inputStream = new FileInputStream(PATH + File.separator + name);
		Converter converter = new Converter();
		converter.convertY2X(inputStream);
		TServiceTemplate serviceTemplate = RepositoryFactory.getRepository().getElement(
			new ServiceTemplateId(
				new Namespace(Namespaces.DEFAULT_NS, false),
				new XmlId("Showcase", false)
			)
		);

		Assert.assertNotNull(serviceTemplate);
	}
}
