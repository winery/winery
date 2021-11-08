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
import { ToastrService } from 'ngx-toastr';

@Injectable()
export class ErrorHandlerService {

    constructor(private alert: ToastrService) {
    }

    /**
     * Error handler.
     * @param error    the error
     */
    handleError(error: HttpErrorResponse) {
        const errorMessage = error.message ? error.message : '';
        if (error.error instanceof ErrorEvent) {
            this.alert.error('Status: '
                + error.statusText + ' ' + error.status + '<br/>' + errorMessage, 'Something went wrong!');
        } else {
            this.alert.error('Status: '
                + error.statusText + ' ' + error.status + '<br/>' + errorMessage, 'Something went wrong!');
        }
    }
}
