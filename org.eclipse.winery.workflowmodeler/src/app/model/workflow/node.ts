/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { Position } from './position';
import { Template } from './Template';
import { Parameter } from '../parameter';

export class Node {
    public connection: any = [];
    public id: string;
    public input: Parameter[];
    public name: string;
    public nodeInterface: string;
    public nodeOperation: string;
    public nodeTemplate: string;
    public output: Parameter[];
    public position = new Position();
    public template: Template;
    public type: string;

}
