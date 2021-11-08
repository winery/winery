/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { Injectable } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { InstanceService } from '../instance/instance.service';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { WineryLicense } from './wineryLicense';
import { map } from 'rxjs/internal/operators';
import { WineryRepositoryConfigurationService } from '../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';

@Injectable()
export class WineryLicenseService {

    private _allLicences: WineryLicense[];

    constructor(private http: HttpClient, private sharedData: InstanceService, private configurationService: WineryRepositoryConfigurationService) {
        this._allLicences = [
            new WineryLicense('Apache-2.0', '/assets/licenses/Apache-2.0.txt'),
            new WineryLicense('EPL-2.0', '/assets/licenses/EPL-2.0.txt'),
            new WineryLicense('Proprietary', '/assets/licenses/Proprietary.txt'),
        ];
        if (configurationService.configuration.features.radon) {
            this._allLicences.push(new WineryLicense('RADON', '/assets/licenses/RADON.txt'));
        }
    }

    getData(): Observable<string> {
        const headers = new HttpHeaders({ 'Accept': 'text/plain' });
        return this.http.get(
            this.sharedData.path + '/LICENSE',
            { headers: headers, responseType: 'text' }
        );
    }

    save(licenseFile: String): Observable<HttpResponse<string>> {
        return this.http.put(
            this.sharedData.path + '/LICENSE',
            licenseFile,
            { observe: 'response', responseType: 'text' }
        );
    }

    loadLicenses(): Observable<string[]> {
        // create an array of observables to execute them later on parallel.
        const allObservables = this._allLicences.map(license => {
            const headers = new HttpHeaders({ 'Accept': 'text/plain' });
            return this.http.get(license.LICENSE_FILE_URL, { headers: headers, responseType: 'text' })
                .pipe(
                    map((result: string) => {
                        license.licenceText = result;
                        return result;
                    })
                );
        });

        // execute the observables on parallel
        return forkJoin(allObservables);
    }

    getLicenseText(name: string): string {
        const theLicense = this._allLicences.find(item => item.LICENCE_NAME.toLowerCase() === name.toLowerCase());

        if (theLicense !== null && theLicense !== undefined) {
            return theLicense.licenceText;
        } else {
            return 'License not found!';
        }
    }

    getLicenseNames(): string[] {
        return this._allLicences.map(license => license.LICENCE_NAME);
    }
}
