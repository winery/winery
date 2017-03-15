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
import { NamespaceSelectorService } from '../../namespaceSelector/namespaceSelector.service';
import { NamespacesService } from './namespaces.service';
import { NotificationService } from '../../notificationModule/notificationservice';

@Component({
    selector: 'winery-instance-namespaces',
    templateUrl: 'namespaces.component.html',
    providers: [NamespaceSelectorService, NamespacesService]
})
export class NamespacesComponent implements OnInit {

    adminNamespaces: Array<any> = [];
    columns = [
        {title: 'Prefix', name: 'prefix'},
        {title: 'Namespace', name: 'namespace'}
        ];

    constructor(private service: NamespacesService,
                private notify: NotificationService) {
    }

    ngOnInit() {
        this.service.getAllNamespaces().subscribe(
            data => {
                this.adminNamespaces = data;
                console.log(data);
                // this.loading = false;
            },
            error => this.notify.error(error.toString())
        );
    }
}
