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
import { ROMetadataApiData } from '../../../model/researchObjectApiData';

@Component({
    selector: 'winery-meta-data-research-object',
    templateUrl: 'researchObjectMetaData.component.html'
})
export class ResearchObjectMetaDataComponent implements OnInit {

    data: ROMetadataApiData;
    loading = true;
    private items: Array<string> = ['Agricultural Sciences', 'Arts and Humanities', 'Astronomy and Astrophysics', 'Business and Management',
        'Chemistry', 'Computer and Information Science', 'Earth and Environmental Sciences', 'Engineering', 'Law',
        'Mathematical Sciences', 'Medicine', 'Health and Life Sciences', 'Physics', 'Social Sciences', 'Other'];
    private selection: string[] = [];

    constructor(private service: ResearchObjectService,
                private notify: WineryNotificationService) {
    }

    ngOnInit() {
        this.service.getResearchObjectMetadata()
            .subscribe(
                (data) => {
                    this.handleData(data);
                },
                (error) => {
                    this.handleError(error);
                }
            );
    }

    public itemsToList(value: Array<any> = []): Array<string> {
        const valueList: Array<string> = [];
        for (const entry of value) {
            valueList.push(entry.text);
        }
        return valueList;
    }

    saveResearchObjectMetadata() {
        this.data.subjects = { subject: this.itemsToList(this.selection) };
        this.service.saveResearchObjectMetadata(this.data).subscribe(
            (data) => {
                this.handleSuccess('Saved data');
            },
            (error) => this.handleError(error)
        );
    }

    handleData(data: ROMetadataApiData) {
        this.data = data;
        if (data.subjects) {
            this.selection = this.data.subjects.subject;
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
