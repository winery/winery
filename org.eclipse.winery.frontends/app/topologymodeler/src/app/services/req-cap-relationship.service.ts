/********************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import { BehaviorSubject } from 'rxjs';
import { EntityType } from '../models/ttopology-template';
import { EventEmitter, Output } from '@angular/core';

export class ReqCapRelationshipService {
    @Output() sourceSelectedEvent: EventEmitter<any>;
    @Output() sendSelectedRelationshipTypeEvent: EventEmitter<any>;
    private closedEndpointEvent = new BehaviorSubject<any>('');
    private askForRepaintEvent = new BehaviorSubject<any>('');

    constructor() {
        this.sourceSelectedEvent = new EventEmitter();
        this.sendSelectedRelationshipTypeEvent = new EventEmitter();
    }

    createSourceInfo(currentNodeData: any, reqOrCap: any) {
        const dragSourceInfo = {
            dragSource: reqOrCap,
            nodeId: currentNodeData.nodeTemplate.id
        };
        this.sourceSelectedEvent.emit(dragSourceInfo);
    }

    passCurrentType(currentType: EntityType): void {
        this.sendSelectedRelationshipTypeEvent.emit(currentType);
    }

    closeConnectorEndpoints(currentNodeData: any, reqOrCap: any): void {
        const dragTargetInfo = {
            dragSource: reqOrCap,
            nodeId: currentNodeData.nodeTemplate.id
        };
        this.closedEndpointEvent.next(dragTargetInfo);
        this.repaint(new Event('repaint'));
    }

    closeConnectionEventListener() {
        return this.closedEndpointEvent.asObservable();
    }

    repaint($event) {
        setTimeout(() => this.askForRepaintEvent.next('Repaint'), 1);
    }

    repaintEventListener() {
        return this.askForRepaintEvent.asObservable();
    }

}
