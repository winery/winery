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
import { Component, OnInit } from '@angular/core';
import { SelfServicePortalService } from './selfServicePortal.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { InstanceService } from '../../instance.service';

@Component({
    selector: 'winery-self-service-images',
    templateUrl: 'selfServicePortalImages.component.html'
})
export class SelfServicePortalImagesComponent implements OnInit {
    loading = true;
    iconPath: string;
    imagePath: string;

    constructor(private service: SelfServicePortalService,
                private notify: WineryNotificationService,
                public sharedData: InstanceService) {
    }

    ngOnInit() {
        this.loading = true;
        this.iconPath = this.service.getIconPath();
        this.imagePath = this.service.getImagePath();
        this.loading = false;
    }

    onUploadSuccess(name: string) {
        this.loading = true;
        this.notify.success('Successfully uploaded ' + name);
        const number = Math.random();
        this.iconPath = this.service.getIconPath() + '?' + number;
        this.imagePath = this.service.getImagePath() + '?' + number;
        this.loading = false;
    }
}
