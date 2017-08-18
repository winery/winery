/**
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     ZTE - initial API and implementation and/or initial documentation
 */

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
