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
