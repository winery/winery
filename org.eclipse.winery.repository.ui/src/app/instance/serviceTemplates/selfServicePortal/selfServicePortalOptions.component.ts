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
import { Component } from '@angular/core';
import { ApplicationOption } from './selfServicePortal.service';

@Component({
    selector: 'winery-self-service-portal-options',
    templateUrl: 'selfServicePortalOptions.component.html'
})
export class SelfServicePortalOptionsComponent {

    options: ApplicationOption[] = [];
    columns = [
        { title: 'Name', name: 'name' },
        { title: 'Icon', name: 'icon' },
        { title: 'Plan Service Name', name: 'planServiceName' }
    ];

    onRemoveClick(event: any) {
    }

    onAddClick() {
    }
}
