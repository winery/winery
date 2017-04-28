/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { InputParameters, OutputParameters } from '../../wineryInterfaces/parameters';

export class InterfacesApiData {
    operation: InterfaceOperationApiData[] = [];
    name: string;

    constructor(name = '') {
        this.name = name;
    }
}

export class InterfaceOperationApiData {
    documentation: Array<any> = null;
    any: Array<any> = null;
    otherAttributes: Object = null;
    inputParameters: InputParameters = new InputParameters();
    outputParameters: OutputParameters = new OutputParameters();
    name = '';

    constructor(name = '') {
        this.name = name;
    }
}
