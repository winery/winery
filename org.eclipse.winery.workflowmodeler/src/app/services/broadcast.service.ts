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
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { Node } from '../model/workflow/node';

/**
 * BroadcastService
 * All of the observable subject should be registered to this service.
 * It provider a broadcast method to broadcast data. the broadcast method would catch error while broadcasting.
 */
@Injectable()
export class BroadcastService {

    public jsPlumbInstance = new Subject<any>();
    public jsPlumbInstance$ = this.jsPlumbInstance.asObservable();

    public showProperty = new Subject<boolean>();
    public showProperty$ = this.showProperty.asObservable();

    public planModel = new Subject<Node[]>();
    public planModel$ = this.planModel.asObservable();

    public saveEvent = new Subject<string>();
    public saveEvent$ = this.saveEvent.asObservable();

    public nodeProperty = new Subject<Node>();
    public nodeProperty$ = this.nodeProperty.asObservable();

    /**
     * broadcast datas
     * this method will catch the exceptions for the broadcast
     * @param subject will broadcast data
     * @param data will be broadcated
     */
    public broadcast(subject: Subject<any>, data: any) {
        try {
            subject.next(data);
        } catch (err) {
            console.error(err);
        }
    }
}
