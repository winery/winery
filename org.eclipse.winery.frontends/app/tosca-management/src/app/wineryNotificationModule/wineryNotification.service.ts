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
import { ToastrService } from 'ngx-toastr';
import { DatePipe } from '@angular/common';

@Injectable()
export class WineryNotificationService {

    toastr: ToastrService;
    notifications: Array<WineryNotification> = [];

    constructor(private pToastr: ToastrService,
                private datePipe: DatePipe) {
        this.toastr = pToastr;
    }

    /**
     * returns a List of all previously created notifications
     * @returns {Array<any>} - contains the notification objects
     */
    getHistory(): Array<WineryNotification> {
        return this.notifications;
    }

    /**
     *
     * @param message
     * @param title
     */
    success(message: string, title = 'Success', options = {}) {
        this.toastr.success(message, title, options);
        this.notifications.push({ title: title, message: message, type: 'success', createdOn: this.getCurrentDate() });
    }

    /**
     *
     * @param message
     * @param title
     */
    error(message: string, title = 'Error') {
        this.toastr.error(message, title);
        this.notifications.push({ title: title, message: message, type: 'error', createdOn: this.getCurrentDate() });
    }

    /**
     *
     * @param message
     * @param title
     */
    warning(message: string, title = 'Warning') {
        this.toastr.warning(message, title);
        this.notifications.push({ title: title, message: message, type: 'warning', createdOn: this.getCurrentDate() });
    }

    /**
     * returns the current date
     * @returns {string}
     */
    private getCurrentDate() {
        return this.datePipe.transform(Date.now(), 'short');
    }
}

interface WineryNotification {
    title: string;
    type: string;
    message: string;
    createdOn: string;
}
