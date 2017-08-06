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
    sourceArrowHead: string;
    targetArrowHead: string;
    dash: string;
    color: string;
    hoverColor: string;
    boolData: any;

    public constructor(data: any, create: boolean) {
        this.sourceArrowHead = data.sourceArrowHead;
        this.targetArrowHead = data.targetArrowHead;
        this.dash = data.dash;
        this.color = data.color;
        this.hoverColor = data.hoverColor;
        if (create) {
            this.boolData = new DataInfo();
        }
    }
}

export class DataInfo {
    dashSelected: boolean;
    targetArrowHeadSelected: boolean;
    sourceArrowHeadSelected: boolean;

    public constructor() {
        this.sourceArrowHeadSelected = false;
        this.targetArrowHeadSelected = false;
        this.dashSelected = false;
    }
}
