/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
export class CapOrRegDefinitionsTableData {
    name: string = null;
    type: string = null;
    lowerBound: string = null;
    upperBound: string = null;
    constraints: string = null;
    typeUri: string = null;

    constructor(name: string, type: string, lowerBound: string, upperBound: string, constraints: string, typeUri: string) {
        this.name = name;
        this.type = type;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.constraints = constraints;
        this.typeUri = typeUri;
    }
}
