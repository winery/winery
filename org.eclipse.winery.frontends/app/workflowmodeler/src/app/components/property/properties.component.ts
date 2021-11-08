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

import { AfterViewInit, Component } from '@angular/core';

import { Node } from '../../model/workflow/node';
import { NodeType } from '../../model/workflow/node-type';
import { BroadcastService } from '../../services/broadcast.service';
import { JsPlumbService } from '../../services/jsplumb.service';
import { ModelService } from '../../services/model.service';

/**
 * property component presents information of a workflow node.
 * the presented information can be edit in this component.
 * it may load information dynamically. the content may be different for different node type.
 */
@Component({
    selector: 'b4t-properties',
    styleUrls: ['./properties.component.css'],
    templateUrl: 'properties.component.html',
})
export class WmPropertiesComponent implements AfterViewInit {
    public node: Node;
    public nodeTypes = NodeType;
    public show = false;
    public titleEditing = false;

    constructor(private broadcastService: BroadcastService,
                private modelService: ModelService,
                private jsPlumnService: JsPlumbService) {

    }

    public ngAfterViewInit() {
        this.broadcastService.showProperty$.subscribe(show => this.show = show);
        this.broadcastService.nodeProperty$.subscribe(node => this.node = node);
    }

    public nodeNameChanged() {
        this.titleEditing = !this.titleEditing;
        this.jsPlumnService.jsplumbInstance.repaintEverything();
    }

    public deleteNode() {
        this.show = false;

        this.jsPlumnService.remove(this.node.id);
        this.modelService.deleteNode(this.node.id);
    }
}
