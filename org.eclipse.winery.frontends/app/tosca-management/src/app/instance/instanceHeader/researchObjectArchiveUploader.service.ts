/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
import {
    WineryRepositoryConfigurationService
} from '../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { Observable, Subject } from 'rxjs';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';
import { ToscaComponent } from '../../model/toscaComponent';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ResearchObjectService } from '../serviceTemplates/researchObject/researchObject.service';
import { InstanceService } from '../instance.service';

@Injectable()
export class ResearchObjectArchiveUploaderService {

    constructor(private http: HttpClient,
                private notify: WineryNotificationService,
                private configurationService: WineryRepositoryConfigurationService,
                private roService: ResearchObjectService,
                private iService: InstanceService) {
        this.roService.setBaseUrl(this.iService.path);
    }

    uploadROAR(toscaComponent: ToscaComponent, privacyOption: string): Observable<string> {

        const path = this.iService.path + '/publishROAR';
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.post<HttpResponse<string>>(path,
            { privacyOption },
            { headers: headers, observe: 'response' }
        )
            .map(response => response.headers.get('Location'));
    }


    metadataComplete(): Observable<Array<string>> {
        const subject = new Subject<Array<string>>();
        const missingMetadata: Array<string> = [];

        this.roService.getResearchObjectMetadata()
            .subscribe(
                (data) => {
                    if (!data.title) {
                        missingMetadata.push('Title');
                    }
                    if (!data.author) {
                        missingMetadata.push('Author');
                    }
                    if (!data.contact) {
                        missingMetadata.push('E-Mail');
                    }
                    if (!data.description) {
                        missingMetadata.push('Description');
                    }
                    if (!data.subjects || !data.subjects.subject || !data.subjects.subject.length) {
                        missingMetadata.push('Subjects');
                    }
                    subject.next(missingMetadata);
                },
                (error) => {
                    return Observable.throwError(error);
                }
            );
        return subject.asObservable();
    }

    daRusInformationComplete(): boolean {
        const missingInformation: Array<string> = [];

        if (!this.configurationService.configuration.darus.server) {
            missingInformation.push('Server URL');
        }
        if (!this.configurationService.configuration.darus.apiToken) {
            missingInformation.push('API Token');
        }
        if (!this.configurationService.configuration.darus.dataverse) {
            missingInformation.push('Dataverse');
        }
        if (missingInformation.length > 0) {
            this.notify.error('Missing DaRUS information: ' + missingInformation);
            return false;
        }
        return true;
    }
}

