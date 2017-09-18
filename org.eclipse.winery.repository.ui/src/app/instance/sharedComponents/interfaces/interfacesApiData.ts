/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { InputParameters, OutputParameters } from '../../../wineryInterfaces/parameters';
import { WineryComponent } from '../../../wineryInterfaces/wineryComponent';
import { NodeOperation, PlanOperation, RelationshipOperation } from './targetInterface/operations';

export class InterfacesApiData {
    operation: InterfaceOperationApiData[] = [];
    name: string;
    id: string;
    text: string;

    constructor(name = '') {
        this.name = name;
    }
}

export class InterfaceOperationApiData extends WineryComponent {
    inputParameters: InputParameters = new InputParameters();
    outputParameters: OutputParameters = new OutputParameters();
    nodeOperation: NodeOperation;
    relationshipOperation: RelationshipOperation;
    plan: PlanOperation;
}
