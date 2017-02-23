/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/

import { Component, OnInit } from '@angular/core';
import {DocumentationService} from './documentation.service'
import {DocumentationApiData} from './documentationApiData'
import { InstanceService } from '../instance.service';

@Component({
    selector: 'winery-instance-documentation',
    templateUrl: 'documentation.component.html',
    providers:[InstanceService,DocumentationService]
})

export class DocumentationComponent implements OnInit {
    documentationApiData: DocumentationApiData;
    loading: boolean = true;
    constructor(
        private sharedData: InstanceService,
        private service: DocumentationService
    ) {}

    ngOnInit() {
        this.service.getInheritanceData(this.sharedData.path)
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    private handleData(docu: DocumentationApiData) {
        this.documentationApiData = docu;
        this.loading = false;
    }

    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }
}
