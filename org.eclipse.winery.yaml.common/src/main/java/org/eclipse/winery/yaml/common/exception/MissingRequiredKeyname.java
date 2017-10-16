/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.common.exception;

public class MissingRequiredKeyname extends YAMLParserException {
    public MissingRequiredKeyname(String keyname, String context) {
        super("The " + context + " is missing the required keyname \"" + keyname + "\".");
    }
}
