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

import static org.eclipse.winery.model.csar.toscametafile.TOSCAMetaFileAttributes.YAML_CSAR_VERSION_VALUE;

public class YamlTOSCAMetaFileParser extends TOSCAMetaFileParser {

    @Override
    protected int validateCsarVersion(String csarVersion) {
        int errors = 0;

        if (csarVersion == null) {
            this.logAttrMissing(TOSCAMetaFileAttributes.CSAR_VERSION, 0);
            errors++;
        } else if (!(csarVersion.trim()).equals(YAML_CSAR_VERSION_VALUE)) {
            this.logAttrWrongVal(TOSCAMetaFileAttributes.CSAR_VERSION, 0, TOSCAMetaFileAttributes.YAML_CSAR_VERSION_VALUE);
            errors++;
        }
        return errors;
    }
}
