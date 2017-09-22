/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */

import { Component, OnInit } from '@angular/core';
import { ReadmeService } from './readme.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { ArtifactResourceApiData } from '../artifactSource/ArtifactResourceApiData';

@Component({
    templateUrl: 'readme.component.html',
    styleUrls: [],
    providers: [ReadmeService]
})

export class ReadmeComponent implements OnInit {

    loading = true;
    markdownContent = '';

    isEditable = false;

    constructor(private service: ReadmeService, private notify: WineryNotificationService) {

    }

    ngOnInit() {
        this.service.getData().subscribe(
            data => this.markdownContent = data,
            error => this.handleReadmeError()
        );
    }

    saveReadmeFile() {
        const readmeFile = new ArtifactResourceApiData();
        readmeFile.setFileName('README.md');
        readmeFile.setContent(this.markdownContent);
        this.service.save(readmeFile).subscribe(
            data => this.handleSave(),
            error => this.handleError(error)
        );
    }

    private handleError(error: any) {
        this.loading = false;
        this.notify.error(error);
    }

    private handleReadmeError() {
        this.loading = false;
        this.notify.error('No README.md available!');
    }

    private handleSave() {
        this.notify.success('Successfully saved README.md');
    }

}
