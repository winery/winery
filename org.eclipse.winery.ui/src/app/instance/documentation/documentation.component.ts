/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and Implementation
 *     Lukas Balzer, Nicole Keppler - using documentationService to get Data
 *******************************************************************************/

import { Component, OnInit } from '@angular/core';
import { DocumentationService } from './documentation.service';
import { DocumentationApiData } from './documentationApiData';
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
    documentationApiData: DocumentationApiData;
    loading: boolean = true;
    isEmpty: boolean = true;
    constructor(
        private sharedData: InstanceService,
        private service: DocumentationService,

    ) {
        this.documentationApiData = new DocumentationApiData( 'default documentation value' );

    }

    ngOnInit() {
        this.service.getDocumentationData(this.sharedData.path)
            .subscribe(
                data => this.handleData(data),
                error => this.handleError(error)
            );
    }

    private handleData(docu: DocumentationApiData) {
        this.documentationApiData = docu;
        if (this.documentationApiData.documentation === 'empty') {
            this.isEmpty = true;
        } else {
            this.isEmpty = false;
        }
        this.loading = false;
    }



    private saveToServer() {
        this.loading = true;
        this.service.saveDocumentationData(this.documentationApiData, this.isEmpty)
            .subscribe(
                data => this.handleCUResponse(data),
                error => this.handleError(error)
            );
        console.log(this.documentationApiData.documentation);
    }

    private handleCUResponse(response: any) {
        this.loading = false;
        console.log(response);
    }

    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }
}
