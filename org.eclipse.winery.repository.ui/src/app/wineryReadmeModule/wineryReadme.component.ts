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
import {Component, OnInit} from '@angular/core';
import {ReadmeService} from './wineryReadme.service';
import {WineryNotificationService} from '../wineryNotificationModule/wineryNotification.service';
import {InstanceService} from '../instance/instance.service';
import {ToscaTypes} from '../wineryInterfaces/enums';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component({
    templateUrl: 'wineryReadme.component.html',
    styleUrls: ['wineryReadme.component.css'],
    providers: [ReadmeService]
})

export class WineryReadmeComponent implements OnInit {

    loading = true;
    readmeContent = '';
    initialReadmeContent = '';

    isEditable = false;
    readmeAvailable = true;
    toscaType: ToscaTypes;

    constructor(private service: ReadmeService, private notify: WineryNotificationService, public sharedData: InstanceService) {
        this.toscaType = this.sharedData.toscaComponent.toscaType;

    }

    ngOnInit() {
        this.service.getData().subscribe(
            data => {
                this.readmeContent = data;
                this.initialReadmeContent = data;
            },
            () => this.handleMissingReadme()
        );
    }

    saveReadmeFile() {
        this.service.save(this.readmeContent).subscribe(
            () => this.handleSave(),
            error => this.handleError(error)
        );
    }

    cancelEdit() {
        this.isEditable = false;
        this.readmeContent = this.initialReadmeContent;
    }

    private handleError(error: HttpErrorResponse) {
        this.loading = false;
        this.notify.error(error.message);
    }

    private handleMissingReadme() {
        this.loading = false;
        this.readmeAvailable = false;
    }

    private handleSave() {
        this.notify.success('Successfully saved README.md');
    }

}
