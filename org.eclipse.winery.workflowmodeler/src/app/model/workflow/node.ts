/*******************************************************************************
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
import {Parameter} from './parameter';
import {Position} from './position';
import {Template} from './Template';

export class Node {
    public connection = [];
    public id: string;
    public input = new Array<Parameter>();
    public name: string;
    public nodeInterface: string;
    public nodeOperation: string;
    public nodeTemplate: string;
    public output = new Array<Parameter>();
    public position = new Position();
    public template = new Template();
    public type: string;

}
