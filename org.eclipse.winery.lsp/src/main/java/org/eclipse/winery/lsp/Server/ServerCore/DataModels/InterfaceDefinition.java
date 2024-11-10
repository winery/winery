/*******************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.lsp.Server.ServerCore.DataModels;

import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;

import java.util.Optional;

public record InterfaceDefinition(ToscaString type,
                                  Optional<ToscaString> description,
                                  Optional<ToscaMap<String, String>> metadata,
                                  Optional<ToscaMap<String, ParameterDefinition>> inputs,
                                  Optional<ToscaMap<String, OperationDefinition>> operations,
                                  Optional<ToscaMap<String, NotificationDefinition>> notifications) {
}
