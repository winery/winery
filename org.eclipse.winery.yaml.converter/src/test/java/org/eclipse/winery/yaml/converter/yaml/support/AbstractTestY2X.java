/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.yaml.support;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.yaml.common.Utils;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.eclipse.winery.yaml.common.writer.WriterUtils;
import org.eclipse.winery.yaml.converter.Converter;

import org.junit.BeforeClass;

public abstract class AbstractTestY2X {

	private final static String FILE_EXTENSION = ".yml";
	protected final String PATH;
	protected final String OUT_PATH;

	public AbstractTestY2X(String PATH) {
		this.PATH = PATH;
		this.OUT_PATH = PATH + File.separator + "tmp";
	}

	@BeforeClass
	public static void setRepository() {
		RepositoryFactory.getRepository(Utils.getTmpDir("AbstractTests").toPath());
	}

	public String getName(String name) {
		return name + FILE_EXTENSION;
	}

	public TServiceTemplate readServiceTemplate(String name) throws Exception {
		Reader reader = new Reader();
		return reader.parse(PATH, getName(name));
	}

	public TServiceTemplate readServiceTemplate(String name, String namespace) throws Exception {
		Reader reader = new Reader();
		return reader.parse(PATH, getName(name), namespace);
	}

	public TServiceTemplate readServiceTemplate(String path, String name, String namespace) throws Exception {
		Reader reader = new Reader();
		return reader.parse(PATH, path + File.separator + name + FILE_EXTENSION, namespace);
	}

	public Definitions convert(TServiceTemplate serviceTemplate, String name, String namespace) {
		Converter converter = new Converter();
		return converter.convertY2X(serviceTemplate, name, namespace, PATH, PATH + File.separator + "tmp");
	}

	public void writeXml(Definitions definitions, String name, String namespace) throws JAXBException {
		WriterUtils.saveDefinitions(definitions, OUT_PATH, namespace, name);
	}
}
