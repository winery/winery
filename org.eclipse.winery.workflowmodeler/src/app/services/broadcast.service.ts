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
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

import { Operation } from '../model/operation';
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
