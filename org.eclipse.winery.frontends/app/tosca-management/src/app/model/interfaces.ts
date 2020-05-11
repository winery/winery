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
import { Parameter } from './parameters';

export class Interface {
    name: string;
    type: string;
    inputs: Parameter[] = [];
    operations: Operation[] = [];
}

export class Operation {
    name: string;
    description = '';
    inputs: Parameter[] = [];
    outputs: Parameter[] = [];
    implementation: OperationImplementation;
}

export class OperationImplementation {
    primary: string;
    dependencies: string[];
    // operationHost: string;
    timeout: number;
}

export const StandardInterface: Interface = {
    name: 'Standard', type: '{tosca.interfaces.node.lifecycle}Standard', inputs: [], operations: [
        Object.assign(new Operation(), { name: 'create' }),
        Object.assign(new Operation(), { name: 'configure' }),
        Object.assign(new Operation(), { name: 'start' }),
        Object.assign(new Operation(), { name: 'stop' }),
        Object.assign(new Operation(), { name: 'delete' }),
    ]
};

export const ConfigureInterface: Interface = {
    name: 'Configure', type: '{tosca.interfaces.relationship}Configure', inputs: [], operations: [
        Object.assign(new Operation(), { name: 'pre_configure_source' }),
        Object.assign(new Operation(), { name: 'pre_configure_target' }),
        Object.assign(new Operation(), { name: 'post_configure_source' }),
        Object.assign(new Operation(), { name: 'post_configure_target' }),
        Object.assign(new Operation(), { name: 'add_target' }),
        Object.assign(new Operation(), { name: 'add_source' }),
        Object.assign(new Operation(), { name: 'target_changed' }),
        Object.assign(new Operation(), { name: 'remove_target' }),
    ]
};
