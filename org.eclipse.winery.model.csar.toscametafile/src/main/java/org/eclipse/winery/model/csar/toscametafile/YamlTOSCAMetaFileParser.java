/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.csar.toscametafile;

import java.util.Map;

import org.eclipse.virgo.util.parser.manifest.ManifestContents;

import static org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileAttributes.CSAR_VERSION_VALUE_FOR_YAML;

public class YamlTOSCAMetaFileParser extends TOSCAMetaFileParser {

    @Override
    protected int validateBlock0(ManifestContents mf) {
        int numErrors = 0;
        Map<String, String> mainAttr = mf.getMainAttributes();

        numErrors += validateMetaVersion(mainAttr.get(TOSCAMetaFileAttributes.TOSCA_META_FILE_VERSION));
        numErrors += validateCsarVersion(mainAttr.get(TOSCAMetaFileAttributes.CSAR_VERSION));
        numErrors += validateCreatedBy(mainAttr.get(TOSCAMetaFileAttributes.CREATED_BY));
        numErrors += validateEntryDefinitions(mainAttr.get(TOSCAMetaFileAttributes.ENTRY_DEFINITIONS));
        numErrors += validateDescription(mainAttr.get(TOSCAMetaFileAttributes.DESCRIPTION));
        numErrors += validateTopology(mainAttr.get(TOSCAMetaFileAttributes.TOPOLOGY));

        return numErrors;
    }

    @Override
    protected int validateMetaVersion(String metaFileVersion) {
        int errors = 0;

        if (metaFileVersion == null) {
            this.logAttrMissing(TOSCAMetaFileAttributes.TOSCA_META_FILE_VERSION, 0);
            errors++;
        } else if (!metaFileVersion.trim().equals(TOSCAMetaFileAttributes.TOSCA_META_FILE_VERSION_VALUE)) {
            this.logAttrWrongVal(TOSCAMetaFileAttributes.TOSCA_META_FILE_VERSION, 0, TOSCAMetaFileAttributes.TOSCA_META_FILE_VERSION_VALUE);
            errors++;
        }
        return errors;
    }

    @Override
    protected int validateCsarVersion(String csarVersion) {
        int errors = 0;

        if (csarVersion == null) {
            this.logAttrMissing(TOSCAMetaFileAttributes.CSAR_VERSION, 0);
            errors++;
        } else if (!csarVersion.trim().equals(CSAR_VERSION_VALUE_FOR_YAML)) {
            this.logAttrWrongVal(TOSCAMetaFileAttributes.CSAR_VERSION, 0, TOSCAMetaFileAttributes.CSAR_VERSION_VALUE_FOR_YAML);
            errors++;
        }
        return errors;
    }
}
