/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
import { HttpErrorResponse } from '@angular/common/http';
import { WineryAlertService } from '../winery-alert/winery-alert.service';

@Injectable()
export class ErrorHandlerService {

  constructor(private alert: WineryAlertService) { }

    /**
     * Error handler.
     * @param error    the error
     */
    handleError(error: HttpErrorResponse) {
        if (error.error instanceof ErrorEvent) {
            this.alert.info('<p>Something went wrong! <br>' + 'Response Status: '
                + error.statusText + ' ' + error.status + '</p><br>' + error.error.message);
        } else {
            this.alert.info('<p>Something went wrong! <br>' + 'Response Status: '
                + error.statusText + ' ' + error.status + '</p><br>' + error.error);
        }
    }
}
