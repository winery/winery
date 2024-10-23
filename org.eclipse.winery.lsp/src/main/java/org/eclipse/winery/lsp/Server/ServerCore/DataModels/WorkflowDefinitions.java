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
/**
 * Workflow Definition
 * For more details on the TOSCA specification, visit:
 * <a href="https://docs.oasis-open.org/tosca/TOSCA/v2.0/csd06/TOSCA-v2.0-csd06.html#13-workflows">Workflow Definition</a>
 */
package org.eclipse.winery.lsp.Server.ServerCore.DataModels;

import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaMap;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;
import java.util.Map;
import java.util.Optional;

public record WorkflowDefinitions(Optional<ToscaString> description,
                                  Optional<ToscaMap<String, Object>> metadata,
                                  Optional<ToscaMap<String, ParameterDefinition>> inputs,
                                  Optional<ToscaMap<String, ParameterDefinition>> outputs,
                                  Optional<Object> precondition,//TODO add function stack that represents the condition clause that must evaluate to true before the workflow can be processed.
                                  Optional<Map<String, WorkflowStepDefinition>> steps,
                                  Optional<OperationAndNotificationImplementationDefinition> implementation) { }
