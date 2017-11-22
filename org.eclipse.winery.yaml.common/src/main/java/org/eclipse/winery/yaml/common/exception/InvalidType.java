/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.common.exception;

import org.yaml.snakeyaml.constructor.ConstructorException;

public class InvalidType extends YAMLParserException {
    private final ConstructorException constructorException;

    public InvalidType(String property, ConstructorException e) {
        super("The property=\"" + property + "\" has an InvalidType");
        this.constructorException = e;
    }

    public String toString() {
        return this.getMessage() + "\n" + constructorException.toString();
    }
}
