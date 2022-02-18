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
import { ResourceSupport } from './resource-support.model';
import { InputParameter } from './input-parameter.model';
import { OutputParameter } from './output-parameter.model';
import { PlanLogEntry } from './plan-log-entry.model';

export class PlanInstance extends ResourceSupport {
    correlation_id: string;
    service_template_instance_id: number;
    state: string;
    type: string;
    inputs: Array<InputParameter>;
    outputs: Array<OutputParameter>;
    logs: Array<PlanLogEntry>;
}
