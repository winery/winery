/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Component, OnInit } from '@angular/core';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';

@Component({
    selector: 'winery-instance-logger-component',
    templateUrl: 'logger.component.html',
    styleUrls: [
        'logger.component.css'
    ],
})
export class LoggerComponent implements OnInit {

    logData: Array<any> = [];
    columns = [
        {title: 'Type', name: 'type'},
        {title: 'Title', name: 'title'},
        {title: 'Message', name: 'message'},
        {title: 'Date', name: 'createdOn'}
    ];

    constructor(private notify: WineryNotificationService) {
    }

    ngOnInit() {
        this.logData = this.notify.getHistory();
    }
}
