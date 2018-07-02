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

import { BroadcastService } from '../../services/broadcast.service';
import { JsPlumbService } from '../../services/jsplumb.service';
import { ModelService } from '../../services/model.service';

/**
 * main canvas, it contains two parts: canvas and node property component
 * bpmn task nodes can be dropped into this canvas, and then the workflow can be edit
 */
@Component({
    selector: 'b4t-container',
    styleUrls: ['./container.component.css'],
    templateUrl: 'container.component.html',
})
export class WmContainerComponent implements AfterViewInit {

    constructor(private broadcastService: BroadcastService,
                private jsPlumbService: JsPlumbService,
                public modelService: ModelService) {

        this.broadcastService.jsPlumbInstance$.subscribe(instance => this.jsPlumbService.buttonDroppable());
    }

    public ngAfterViewInit() {
        setTimeout(() => this.jsPlumbService.initJsPlumbInstance(), 0);
    }

    public canvasClick() {
        this.broadcastService.broadcast(this.broadcastService.showProperty, false);
    }

}
