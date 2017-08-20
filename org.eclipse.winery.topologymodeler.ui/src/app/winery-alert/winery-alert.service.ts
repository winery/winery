/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Yannic Sowoidnich - initial API and implementation
 */
import {Injectable, ViewContainerRef} from '@angular/core';
import {ToastsManager} from 'ng2-toastr/ng2-toastr';
import {DatePipe} from '@angular/common';

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
  success(message: string, title = 'success') {
    this.toastr.success(message, title);
    this.alerts.push({title: title, message: message, type: 'success', createdOn: this.getCurrentDate()});

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
