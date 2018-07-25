/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/

import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { IWineryState } from '../../redux/store/winery.store';
import { NgRedux } from '@angular-redux/store';
import { WineryActions } from '../../redux/actions/winery.actions';

@Component({
    selector: 'winery-target-locations',
    templateUrl: './target-locations.component.html',
    styleUrls: ['./target-locations.component.css']
})
export class TargetLocationsComponent implements OnInit, OnChanges {

    properties: Subject<string> = new Subject<string>();
    @Input() currentNodeData: any;
    @Output() sendTargetLocation: EventEmitter<any>;
    targetLocation: string;
    subscriptionTargetLocation: Subscription;

    constructor(private $ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions) {
    }

    /**
     * Angular lifecycle event.
     */
    ngOnChanges(changes: SimpleChanges) {
        if (changes) {
            this.checkForTargetLocations();
        }
    }

    /**
     * Assigns the target location value for display.
     */
    private checkForTargetLocations(): void {
        let localName: string;
        for (const key in this.currentNodeData.currentTargetLocation) {
            if (this.currentNodeData.currentTargetLocation.hasOwnProperty(key)) {
                localName = key.substring(key.indexOf('}') + 1);
                if (localName === 'location' || key === 'location') {
                    this.targetLocation = this.currentNodeData.currentTargetLocation[key];
                    break;
                }
            }
        }
    }

    /**
     * Angular lifecycle event.
     */
    ngOnInit() {
        this.checkForTargetLocations();

        // set target location with a debounceTime of 300ms
        this.subscriptionTargetLocation = this.properties.pipe(
            debounceTime(300),
            distinctUntilChanged(), )
            .subscribe(value => {
                this.$ngRedux.dispatch(this.actions.setTargetLocation({
                    nodeId: this.currentNodeData.currentNodeId,
                    newTargetLocation: value
                }));
            });
    }

}
