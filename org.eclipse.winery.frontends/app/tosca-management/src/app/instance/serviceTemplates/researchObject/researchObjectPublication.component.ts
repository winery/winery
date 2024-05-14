/*******************************************************************************
 * Copyright (c) 2021-2022 Contributors to the Eclipse Foundation
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
 *******************************************************************************/
import { Component, OnInit } from '@angular/core';
import { ResearchObjectService } from './researchObject.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ROPublicationApiData } from '../../../model/researchObjectApiData';

@Component({
    selector: 'winery-publication-research-object',
    templateUrl: 'researchObjectPublication.component.html'
})
export class ResearchObjectPublicationComponent implements OnInit {

    data: ROPublicationApiData;
    loading = true;
    public items: Array<string> = ['ark', 'arXiv', 'bibcode', 'doi', 'ean13', 'eissn', 'handle',
        'isbn', 'issn', 'istc', 'lissn', 'lsid', 'pmid', 'purl', 'upc', 'url', 'urn'];
    private idType: any = [];

    constructor(private service: ResearchObjectService,
                private notify: WineryNotificationService) {
    }


    ngOnInit() {
        this.service.getResearchObjectPublication()
            .subscribe(
                (data) => {
                    this.handleData(data);
                },
                (error) => {
                    this.handleError(error);
                }
            );
    }

    saveResearchObjectPublication() {
        if (!!this.idType[0]) {
            this.data.idType = this.idType[0].text;
        }
        this.service.saveResearchObjectPublication(this.data).subscribe(
            (data) => {
                this.handleSuccess('Saved data');
            },
            (error) => {
                this.handleError(error);
            }
        );
    }

    handleData(data: ROPublicationApiData) {
        this.data = data;
        if (!!this.data.idType) {
            this.idType.push(this.data.idType);
        }
        this.loading = false;
    }

    handleSuccess(message: string) {
        this.notify.success(message);
        this.loading = false;
    }

    handleError(error: HttpErrorResponse) {
        this.notify.error(error.message);
        this.loading = false;
    }

}
