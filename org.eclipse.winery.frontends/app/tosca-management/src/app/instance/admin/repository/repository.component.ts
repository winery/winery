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
import { Component, OnInit, ViewChild } from '@angular/core';
import { RepositoryService } from './repository.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { backendBaseURL } from '../../../configuration';
import { ModalDirective } from 'ngx-bootstrap';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'winery-instance-repository',
    templateUrl: 'repository.component.html'
})
export class RepositoryComponent implements OnInit {

    @ViewChild('uploaderModal') uploaderModal: ModalDirective;
    path: string;

    constructor(private service: RepositoryService,
                private notify: WineryNotificationService) {
    }

    ngOnInit() {
        this.path = backendBaseURL + this.service.path + '/';
    }

    clearRepository() {
        this.service.clearRepository().subscribe(
            () => this.handleSuccess('Repository cleared'),
            error => this.handleError(error)
        );
    }

    handleSuccess(message: string) {
        this.notify.success(message);
    }

    handleError(error: HttpErrorResponse) {
        this.notify.error(error.message);
    }

}
