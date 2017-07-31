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
 */
import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';

@Injectable()
export class EntityContainterService {

    constructor(private http: Http, private notify: WineryNotificationService) {
    }

    deleteComponent(url: string, id: string) {
        this.http.delete(url)
            .subscribe(
                data => this.notify.success('Successfully deleted ' + id),
                error => this.notify.error('Error deleting ' + id)
            );
    }
}
