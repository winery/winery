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
import { AfterViewInit, Component, Input, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';

import { Node } from '../../model/workflow/node';
import { BroadcastService } from '../../services/broadcast.service';
import { JsPlumbService } from '../../services/jsplumb.service';

/**
 * node component represent a single workflow node.
 * every node would be rendered on the container component
 */
@Component({
    selector: 'b4t-node',
    styleUrls: ['./node.component.css'],
    templateUrl: 'node.component.html',
})
export class WmNodeComponent implements AfterViewInit, OnDestroy {
    @Input() public node: Node;
    @Input() private last: boolean;

    private selected = false;

    private jsPlumbInstanceSubscription: Subscription;
    private nfForJsPlumbInstanceSubscription: Subscription;

    constructor(private jsPlumbService: JsPlumbService,
                private broadcastService: BroadcastService) {
    }

    public ngAfterViewInit() {
        if (this.jsPlumbService.jsplumbInstance) {
            this.jsPlumbService.initNode(this.node);
        } else {
            this.jsPlumbInstanceSubscription = this.broadcastService.jsPlumbInstance$
                .subscribe(instance => this.jsPlumbService.initNode(this.node));
        }

        if (this.last) {
            if (this.jsPlumbService.jsplumbInstance) {
                this.jsPlumbService.connectNode();
            } else {
                this.nfForJsPlumbInstanceSubscription = this.broadcastService.jsPlumbInstance$
                    .subscribe(instance => this.jsPlumbService.connectNode());
            }
        }
    }

    public ngOnDestroy() {
        if (this.jsPlumbInstanceSubscription) {
            this.jsPlumbInstanceSubscription.unsubscribe();
        }

        if (this.nfForJsPlumbInstanceSubscription) {
            this.nfForJsPlumbInstanceSubscription.unsubscribe();
        }
    }

    public showProperties() {
        this.broadcastService.broadcast(this.broadcastService.nodeProperty, this.node);
        this.broadcastService.broadcast(this.broadcastService.showProperty, true);
    }
}
