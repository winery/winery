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
import { BackendService } from './backend.service';
import { ErrorHandlerService } from './error-handler.service';
import { ToastrService } from 'ngx-toastr';

@Injectable()
export class ImportTopologyService {

    constructor(private alert: ToastrService) {
    }

    /**
     * Does a POST request to the server with the imported topology template URL + 'merge'.
     * Saves and reloads the window.
     * @param serviceTemplate   the selected service template to fetch data from
     * @param backendService    the backend service for calling methods to interact with the server
     * @param errorHandler      the error handler which handles failed server requests
     */
    importTopologyTemplate(serviceTemplate: string, backendService: BackendService, errorHandler: ErrorHandlerService): void {
        this.alert.info('', 'Import topology in progress...');
        backendService.importTopology(serviceTemplate).subscribe(res => {
                if (res.ok) {
                    const url = res.headers.get('location');
                    this.alert.success('', 'Successfully imported topology.');
                    window.location.reload();
                }
            },
            error => {
                errorHandler.handleError(error);
            });
    }
}
