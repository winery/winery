/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.tools.deployablecomponents.fileanalyzer.CommandAnalyzer;

import org.eclipse.winery.tools.deployablecomponents.commons.Component;

import java.util.List;

/* Extend this interface and add corresponding implementations to support more command packages
 */
public interface CommandAnalyzer {
    enum Commands {
        AptGet,
        Pip3,
        Yum,
        Npm,
        Apk,
        Chmod,
        Pip;

        public String asString() {
            // all string contain a space at the end to avoid false-positives with string, that contain these letters in a bigger word
            switch (this) {
                case AptGet:
                    return "apt-get ";
                case Pip3:
                    return "pip3 ";
                case Yum:
                    return "yum ";
                case Npm:
                    return "npm ";
                case Apk:
                    return "apk ";
                case Chmod:
                    return "chmod ";
                case Pip:
                    return "pip ";
            }
            return "";
        }
    }

    List<Component> analyze(String command);
}
