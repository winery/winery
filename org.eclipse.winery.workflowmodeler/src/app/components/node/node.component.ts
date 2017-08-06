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
