/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Balzer, Nicole Keppler - initial API and implementation
 */

import { Component, OnInit } from '@angular/core';
import { DocumentationService } from './documentation.service';
import { InstanceService } from '../instance.service';

@Component({
    selector: 'winery-instance-documentation',
    templateUrl: 'documentation.component.html',
    providers: [ DocumentationService ],
    styleUrls: [
        'documentation.component.css'
    ]
})

export class DocumentationComponent implements OnInit {
    documentationData: string;
    loading: boolean = true;
    constructor(
        private sharedData: InstanceService,
        private service: DocumentationService,

    ) {
        this.documentationData = 'default documentation value';

    }

    ngOnInit() {
        this.service.getDocumentationData(this.sharedData.path)
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    private handleData(docu: string) {
        this.documentationData = docu;
        this.loading = false;
    }



    private saveToServer() {
        this.loading = true;
        this.service.saveDocumentationData(this.documentationData)
            .subscribe(
                data => this.handleResponse(data),
                error => this.handleError(error)
            );
    }

    private handleResponse(response: any) {
        this.loading = false;
    }

    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }
}
