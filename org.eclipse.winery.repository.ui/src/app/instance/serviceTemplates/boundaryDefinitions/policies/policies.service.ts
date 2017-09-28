/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions, Response } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { SelectItem } from 'ng2-select';
import { WineryComponent } from '../../../../wineryInterfaces/wineryComponent';
import { backendBaseURL } from '../../../../configuration';

@Injectable()
export class PoliciesService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = backendBaseURL + this.route.url;
    }

    getPolicies(): Observable<WineryPolicy[]> {
        return this.get(this.path);
    }

    getPolicyTypes(): Observable<SelectItem[]> {
        return this.get('/policytypes?grouped=angularSelect');
    }

    getPolicyTemplatesForType(pT: SelectItem): Observable<SelectItem[]> {
        const qName = pT.id.slice(1).split('}');
        return this.get('/policytypes/' + encodeURIComponent(encodeURIComponent(qName[0])) + '/' + qName[1] + '/instances');
    }

    postPolicy(xml: string): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/xml'});
        const options = new RequestOptions({headers: headers});

        return this.http.post(this.path, xml, options);
    }

    deletePolicy(id: string): Observable<Response> {
        return this.http.delete(this.path + '/' + id);
    }

    private get(p: string): Observable<any> {
        const headers = new Headers({'Accept': 'application/json'});
        const options = new RequestOptions({headers: headers});

        return this.http.get(p, options)
            .map(res => res.json());
    }

}

export class WineryPolicy extends WineryComponent {
    policyType: string;
    policyRef: string;
    id: string;
}
