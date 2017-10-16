/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit, ViewChild, ViewContainerRef } from '@angular/core';
import { WineryNotificationService } from './wineryNotificationModule/wineryNotification.service';
import { WineryGitLogComponent } from './wineryGitLog/wineryGitLog.component';
import { ExistService } from './wineryUtils/existService';
import { backendBaseURL } from './configuration';

@Component({
    selector: 'winery-repository',
    templateUrl: './wineryRepository.html',
    styleUrls: ['./wineryRepository.component.css'],
    providers: [
        ExistService
    ]
})
/*
 * This component represents the root component for the Winery Repository.
 */
export class WineryRepositoryComponent implements OnInit {
    // region variables
    name = 'Winery Repository';
    isBackendAvailable = false;
    loading = true;
    @ViewChild('gitLog') gitLog: WineryGitLogComponent;

    // endregion
    options = {
        position: ['top', 'right'],
        timeOut: 3000,
        lastOnBottom: true
    };

    constructor(vcr: ViewContainerRef, private notify: WineryNotificationService, private existService: ExistService) {
        this.notify.init(vcr);
    }

    ngOnInit() {
        this.existService.check(backendBaseURL + '/').subscribe(
            data => {
                this.isBackendAvailable = true;
                this.loading = false;
            },
            error => this.loading = false
        );
    }

    onClick() {
        this.gitLog.hide();
    }

    refresh() {
        window.location.reload();
    }

}
