/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 *     Niko Stadelmaier - add notifications module
 */
import { Component, OnInit, ViewContainerRef } from '@angular/core';
import { WineryNotificationService } from './wineryNotificationModule/wineryNotification.service';

@Component({
    selector: 'winery-repository',
    templateUrl: './wineryRepository.html',
})
/*
 * This component represents the root component for the Winery Repository.
 */
export class WineryRepositoryComponent {
    // region variables
    name = 'Winery Repository';
    // endregion
    options = {
        position: ['top', 'right'],
        timeOut: 3000,
        lastOnBottom: true
    };

    constructor(vcr: ViewContainerRef, private notify: WineryNotificationService) {
        this.notify.init(vcr);
    }
}
