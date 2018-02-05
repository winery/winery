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
import { Component } from '@angular/core';

import { NodeType } from '../../model/workflow/node-type';
import { BroadcastService } from '../../services/broadcast.service';
import { JsPlumbService } from '../../services/jsplumb.service';
import { ModelService } from '../../services/model.service';

/**
 * toolbar component contains some basic operations(save) and all of the supported workflow nodes.
 * The supported nodes can be dragged to container component. which will add a new node to the workflow.
 */
@Component({
    selector: 'b4t-toolbar',
    styleUrls: ['./toolbar.component.css'],
    templateUrl: 'toolbar.component.html',
})
export class WmToolbarComponent {

    nodeTypes = NodeType;

    constructor(private modelSerivce: ModelService,
                private jsPlumbService: JsPlumbService,
                private broadcastSerice: BroadcastService) {
        this.broadcastSerice.jsPlumbInstance$.subscribe(
            instance => this.jsPlumbService.buttonDraggable());
    }

    public save() {
        this.modelSerivce.save();
    }
}
