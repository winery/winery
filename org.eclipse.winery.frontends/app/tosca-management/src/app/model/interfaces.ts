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
        Object.assign(new Operation(), { name: 'create', description: 'The standard create operation' }),
        Object.assign(new Operation(), { name: 'configure', description: 'The standard configure operation' }),
        Object.assign(new Operation(), { name: 'start', description: 'The standard start operation' }),
        Object.assign(new Operation(), { name: 'stop', description: 'The standard stop operation' }),
        Object.assign(new Operation(), { name: 'delete', description: 'The standard delete operation' }),
    ]
};

export const ConfigureInterface: Interface = {
    name: 'Configure', type: '{tosca.interfaces.relationship}Configure', inputs: [], operations: [
        Object.assign(new Operation(), { name: 'pre_configure_source', description: 'The standard pre_configure_source operation' }),
        Object.assign(new Operation(), { name: 'pre_configure_target', description: 'The standard pre_configure_target operation' }),
        Object.assign(new Operation(), { name: 'post_configure_source', description: 'The standard post_configure_source operation' }),
        Object.assign(new Operation(), { name: 'post_configure_target', description: 'The standard post_configure_target operation' }),
        Object.assign(new Operation(), { name: 'add_target', description: 'The standard add_target operation' }),
        Object.assign(new Operation(), { name: 'add_source', description: 'The standard add_source operation' }),
        Object.assign(new Operation(), { name: 'target_changed', description: 'The standard target_changed operation' }),
        Object.assign(new Operation(), { name: 'remove_target', description: 'The standard remove_target operation' }),
    ]
};
