/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Component, OnInit } from '@angular/core';
import { RepositoryService } from './repository.service';
import { NotificationService } from '../../../notificationModule/notification.service';
import { backendBaseUri } from '../../../configuration';

@Component({
    selector: 'winery-instance-repository',
    templateUrl: 'repository.component.html'
})
export class RepositoryComponent implements OnInit {

    path: string;

    constructor(private service: RepositoryService,
                private notify: NotificationService) {
    }

    ngOnInit() {
        this.path = backendBaseUri + this.service.path + '/';
    }

    clearRepository() {
        this.service.clearRepository().subscribe(
            data => this.handleSuccess('Repository cleared'),
            error => this.handleError(error)
        );
    }

    handleSuccess(message: string) {
        this.notify.success(message);
    }

    handleError(error: Error) {
        this.notify.error(error.toString());
    }

}
