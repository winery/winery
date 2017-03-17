/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */

import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../notificationModule/notificationservice';

@Component({
    selector: 'winery-instance-logger-component',
    templateUrl: 'logger.component.html'
})
export class LoggerComponent implements OnInit {

    logData: Array<any> = [];
    columns = [
        {title: 'Type', name: 'type'},
        {title: 'Title', name: 'title'},
        {title: 'Message', name: 'message'},
        {title: 'Date', name: 'createdOn'}
        ];
    constructor(private notify: NotificationService) { }

    ngOnInit() {
        this.logData = this.notify.getHistory();
    }
}
