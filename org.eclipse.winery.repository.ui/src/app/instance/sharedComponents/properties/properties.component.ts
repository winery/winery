/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { Component, OnInit } from '@angular/core';
import { PropertiesService } from './properties.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';

@Component({
    selector: 'winery-properties',
    templateUrl: 'properties.component.html',
    styleUrls: [
        'properties.component.css'
    ],
    providers: [
        PropertiesService
    ]
})
export class PropertiesComponent implements OnInit {

    /**
     * Why `any`? => see {@link PropertiesService.getProperties()}
     */
    properties: any = null;
    propertyKeys: string[];
    loading = true;

    constructor(private service: PropertiesService, private notify: WineryNotificationService) {
    }

    ngOnInit() {
        this.getProperties();
    }

    save() {
        this.loading = true;
        this.service.saveProperties(this.properties)
            .subscribe(
                data => this.handleSave(),
                error => this.handleError(error)
            );
    }

    private getProperties() {
        this.service.getProperties()
            .subscribe(
                data => this.handleProperties(data),
                error => this.loading = false
            );
    }

    private handleSave() {
        this.notify.success('Successfully updated properties!');
        this.getProperties();
    }

    private handleProperties(data: any) {
        this.loading = false;
        this.propertyKeys = Object.keys(data);
        if (this.propertyKeys.length > 0) {
            this.properties = data;
        }
    }

    private handleError(error: any) {
        this.loading = false;
        this.notify.error(error);
    }
}
