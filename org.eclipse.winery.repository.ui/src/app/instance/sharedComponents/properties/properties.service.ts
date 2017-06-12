/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { Injectable } from '@angular/core';
import { Headers, Http, Response, RequestOptions } from '@angular/http';
import { InstanceService } from '../../instance.service';
import { Observable } from 'rxjs/Observable';
import { backendBaseURL } from '../../../configuration';

@Injectable()
export class PropertiesService {

    path: string;

    constructor(private http: Http,
                private sharedData: InstanceService) {
        this.path = backendBaseURL + this.sharedData.path + '/properties/';
    }

    /**
     * We use `any` as return value because the backend delivers the json object containing the property as a key
     * and the value the value. Example: { "property": "this is my property" }.
     */
    public getProperties(): Observable<any> {
        return this.http.get(this.path)
            .map(res => res.json());
    }

    public saveProperties(properties: any): Observable<Response> {
        const headers = new Headers({'Content-Type': 'application/xml'});
        const options = new RequestOptions({headers: headers});
        /* because the backend doesn't support saving json requests yet, we need to construct an xml file and
         * put it to the server
         */
        const keys = Object.keys(properties);
        let xml = '<?xml version="1.0" encoding="utf-8" ?>\n'
            + '<Properties xmlns="http://docs.oasis-open.org/tosca/ns/2011/12">\n\t'
            + '<Properties xmlns="http://opentosca.org/artifacttypes/propertiesdefinition/winery">\n\t';

        for (const key of keys) {
            xml += '\t<' + key + '>';
            if (properties[key]) {
                xml += '<![CDATA[' + properties[key] + ']]>';
            }
            xml += '</' + key + '>\n\t';
        }

        xml += '</Properties>\n</Properties>';

        return this.http.put(this.path, xml, options);
    }
}
