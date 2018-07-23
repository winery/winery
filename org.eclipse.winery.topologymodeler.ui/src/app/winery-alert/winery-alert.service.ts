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

import { Injectable, ViewContainerRef } from '@angular/core';
import { ToastsManager } from 'ng2-toastr/ng2-toastr';
import { DatePipe } from '@angular/common';

/**
 * This is the own internal notify service of the topology renderer - may be replaced by the winery one.
 * @class
 */
@Injectable()
export class WineryAlertService {

    toastr: ToastsManager;
    alerts: Array<WineryAlert> = [];

    constructor(private pToastr: ToastsManager,
                private datePipe: DatePipe) {
        this.toastr = pToastr;
    }

    /**
     * Initializes the Notification Service
     * Important: this function must be called before using the the service
     *
     * @param rootVcr - View Container Reference of the root component
     */
    init(rootVcr: ViewContainerRef) {
        this.toastr.setRootViewContainerRef(rootVcr);

    }

    /**
     *
     * @param message
     * @param title
     */
    success(message: string, title = 'Success') {
        this.toastr.success(message, title);
        this.alerts.push({ title: title, message: message, type: 'success', createdOn: this.getCurrentDate() });

    }

    /**
     *
     * @param message
     * @param title
     */
    info(message: string, title = 'Information') {
        this.toastr.info(message, title);
        this.alerts.push({ title: title, message: message, type: 'info', createdOn: this.getCurrentDate() });

    }

    error(message: string, title = 'Error') {
        this.toastr.error(message, title);
        this.alerts.push({ title: title, message: message, type: 'error', createdOn: this.getCurrentDate() });
    }

    /**
     * returns the current date
     * @returns {string}
     */
    getCurrentDate() {
        return this.datePipe.transform(Date.now(), 'short');
    }
}

interface WineryAlert {
    title: string;
    type: string;
    message: string;
    createdOn: string;
}
