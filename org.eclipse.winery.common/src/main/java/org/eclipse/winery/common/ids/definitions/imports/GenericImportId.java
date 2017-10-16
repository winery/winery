/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common.ids.definitions.imports;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XmlId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TImport;

import org.apache.commons.io.FilenameUtils;

/**
 * class for import ids (not used for definitions)
 *
 * // Convention: id of import is filename without extension
 */
public class GenericImportId extends DefinitionsChildId {

	private final String type;


	/**
	 * @param type the importType (e.g., MimeTypes.MIMETYPE_XSD)
	 */
	public GenericImportId(Namespace namespace, XmlId xmlId, String type) {
		super(namespace, xmlId);
		this.type = type;
	}

	public GenericImportId(String ns, String id, boolean encoded, String type) {
		super(ns, id, encoded);
		this.type = type;
	}

	/**
	 * Generates an ImportId based on an TImport object The import has to be an
	 * import created by winery. This method uses the convention that the id is
	 * derived from the location
	 *
	 * @param i the TImport element to derive an id from
	 */
	public GenericImportId(TImport i) {
		this(i.getNamespace(), GenericImportId.getId(i), false, i.getImportType());
	}

	private static String getId(TImport i) {
		String fileName = Util.getLastURIPart(i.getLocation());
		return FilenameUtils.removeExtension(fileName);
	}

	public String getType() {
		return this.type;
	}

	@Override
	public String getGroup() {
		return "GenericImport";
	}
}
