/*******************************************************************************
 * Copyright (c) 2013 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
package org.eclipse.winery.model.ids.definitions.imports;

import org.apache.commons.io.FilenameUtils;

import org.eclipse.winery.model.ids.IdUtil;
import org.eclipse.winery.model.ids.Namespace;
import org.eclipse.winery.model.ids.XmlId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.tosca.TImport;

/**
 * class for import ids (not used for definitions)
 * <p>
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
        String fileName = IdUtil.getLastURIPart(i.getLocation());
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
