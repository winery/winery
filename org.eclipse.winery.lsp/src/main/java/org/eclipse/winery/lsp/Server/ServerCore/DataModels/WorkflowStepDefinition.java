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
 * Workflow Step Definition
 * For more details on the TOSCA specification, visit:
 * <a href="https://docs.oasis-open.org/tosca/TOSCA/v2.0/csd06/TOSCA-v2.0-csd06.html#1322-workflow-step-definition">Workflow Step Definition</a>
 */
package org.eclipse.winery.lsp.Server.ServerCore.DataModels;

import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaList;
import org.eclipse.winery.lsp.Server.ServerCore.TOSCADataTypes.ToscaString;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

public record WorkflowStepDefinition(ToscaString target,
                                     Optional<ToscaString> target_relationship,
                                     Optional<Stack<Map<String, List<String>>>> filter,
                                     ToscaList<Object> activities,//TODO Activity definitions
                                     Optional<ToscaList<String>> on_success,
                                     Optional<ToscaList<String>> on_failure
                                     ) { }
