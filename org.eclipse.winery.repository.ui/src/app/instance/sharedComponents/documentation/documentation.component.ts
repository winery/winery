/*******************************************************************************
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
 *******************************************************************************/
import { Component, OnInit } from '@angular/core';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { DocumentationService } from './documentation.service';
import { InstanceService } from '../../instance.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component({
    selector: 'winery-instance-documentation',
    templateUrl: 'documentation.component.html',
    providers: [DocumentationService],
    styleUrls: [
        'documentation.component.css'
    ]
})
export class DocumentationComponent implements OnInit {

    documentationData: string;
    loading = true;

    constructor(public sharedData: InstanceService,
                private service: DocumentationService, private notify: WineryNotificationService) {
        this.documentationData = 'default documentation value';
    }

    ngOnInit() {
        this.service.getDocumentationData()
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    saveToServer() {
        this.loading = true;
        this.service.saveDocumentationData(this.documentationData)
            .subscribe(
                data => this.handleResponse(data),
                error => this.handleError(error)
            );
    }

    private handleData(docu: string) {
        this.documentationData = docu;
        this.loading = false;
    }

    private handleResponse(response: HttpResponse<string>) {
        this.loading = false;
        this.notify.success('Successfully saved Documentation!');
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message);
    }
}
