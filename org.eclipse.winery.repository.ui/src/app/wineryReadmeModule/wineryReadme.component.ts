/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit } from '@angular/core';
import { ReadmeService } from './wineryReadme.service';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../instance/instance.service';
import { ToscaTypes } from '../wineryInterfaces/enums';

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

    constructor(private service: ReadmeService, private notify: WineryNotificationService, private sharedData: InstanceService) {
        this.toscaType = this.sharedData.toscaComponent.toscaType;

    }

    ngOnInit() {
        this.service.getData().subscribe(
            data => {
                this.readmeContent = data;
                this.initialReadmeContent = data;
            },
            error => this.handleMissingReadme()
        );
    }

    saveReadmeFile() {
        this.service.save(this.readmeContent).subscribe(
            data => this.handleSave(),
            error => this.handleError(error)
        );
    }

    cancelEdit() {
        this.isEditable = false;
        this.readmeContent = this.initialReadmeContent;
    }

    private handleError(error: any) {
        this.loading = false;
        this.notify.error(error);
    }

    private handleMissingReadme() {
        this.loading = false;
        this.readmeAvailable = false;
    }

    private handleSave() {
        this.notify.success('Successfully saved README.md');
    }

}
