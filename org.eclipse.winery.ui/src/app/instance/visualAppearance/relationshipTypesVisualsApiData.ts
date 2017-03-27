/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer - initial API and implementation
 */

export class RelationshipTypesVisualsApiData {
    sourcearrowhead: string;
    targetarrowhead: string;
    dash: string;
    color: string;
    hovercolor: string;
    boolData: any;

    public constructor(data: any, createBools: boolean) {
        this.sourcearrowhead = data.sourcearrowhead;
        this.targetarrowhead = data.targetarrowhead;
        this.dash = data.dash;
        this.color = data.color;
        this.hovercolor = data.hovercolor;
        if (createBools) {
            this.boolData = new DataInfo();
        }
    }
}

export class DataInfo {
    dashSelected: boolean;
    targetarrowheadSelected: boolean;
    sourcearrowheadSelected: boolean;

    public constructor() {
        this.sourcearrowheadSelected = false;
        this.targetarrowheadSelected = false;
        this.dashSelected = false;
    }
}


