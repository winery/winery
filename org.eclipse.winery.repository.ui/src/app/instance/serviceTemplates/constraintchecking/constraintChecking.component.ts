/********************************************************************************
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
 ********************************************************************************/
import { Component, OnInit, ViewChild } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { WineryEditorComponent } from '../../../wineryEditorModule/wineryEditor.component';
import { ConstraintCheckingService } from './constraintChecking.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';

@Component({
    templateUrl: 'constraintchecking.component.html',
    providers: [ConstraintCheckingService]
})
export class ConstraintCheckingComponent implements OnInit {

    @ViewChild('editor') editor: WineryEditorComponent;

    checkingResult: string;
    loading = false;

    constructor(private service: ConstraintCheckingService, private notification: WineryNotificationService) {
    }

    ngOnInit() {

    }

    checkConstraints(): void {
        this.loading = true;
        this.service.getCheckingResult().subscribe(data => this.handleXmlData(data),
            error => this.handleError(error));
    }

    private handleXmlData(xml: string) {
        this.loading = false;
        this.checkingResult = xml;
    }

    private handleError(error: string): void {
        this.loading = false;
        this.notification.error(error);
    }
}
