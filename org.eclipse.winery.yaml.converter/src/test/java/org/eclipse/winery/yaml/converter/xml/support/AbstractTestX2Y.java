/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.xml.support;

import java.io.File;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.yaml.common.Utils;
import org.eclipse.winery.yaml.common.reader.xml.Reader;
import org.eclipse.winery.yaml.converter.Converter;

import org.junit.BeforeClass;

public abstract class AbstractTestX2Y {

	private final static String FILE_EXTENSION = ".xml";
	protected final String PATH;
	protected final String OUT_PATH;

	public AbstractTestX2Y(String PATH) {
		this.PATH = PATH;
		this.OUT_PATH = PATH + File.separator + "tmp";
	}

	@BeforeClass
	public static void setRepository() {
		RepositoryFactory.getRepository(Utils.getTmpDir("AbstractTests").toPath());
	}

	public Definitions readDefinitions(String name) throws JAXBException {
		Reader reader = new Reader();
		return reader.parse(PATH + name + FILE_EXTENSION);
	}

	public Map<File, TServiceTemplate> convert(Definitions serviceTemplate, String outPath) {
		Converter converter = new Converter();
		return converter.convertX2Y(serviceTemplate, outPath);
	}
}
