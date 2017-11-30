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

import java.util.List;
import java.util.stream.Collectors;

public class UnrecognizedFieldException extends YAMLParserException {
    public UnrecognizedFieldException(List<String> messages) {
        super("\n   " + messages.stream().collect(Collectors.joining("\n   ")));
    }
}
