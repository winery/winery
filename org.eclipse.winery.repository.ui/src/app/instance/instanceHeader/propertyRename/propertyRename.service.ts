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
import { backendBaseURL } from '../../../configuration';
import { ToscaComponent } from '../../../wineryInterfaces/toscaComponent';
import { Observable } from 'rxjs';

@Injectable()
export class PropertyRenameService {

    private propertyName: string;
    private toscaComponent: ToscaComponent;

    constructor(private http: Http,
                private route: Router) {
    }

    setPropertyName(propertyName: string) {
        this.propertyName = propertyName;
    }

    setToscaComponent(toscaComponent: ToscaComponent) {
        this.toscaComponent = toscaComponent;
    }

    setPropertyValue(value: string): Observable<Response> {
        const headers = new Headers({ 'Content-Type': 'application/json' });
        const options = new RequestOptions({ headers: headers });
        let payload;
        if (this.propertyName === 'localName') {
            payload = {
                localname: value
            };
        } else {
            payload = {
                namespace: value
            };
        }
        return this.http.post(backendBaseURL + this.toscaComponent.path + '/' + this.propertyName, payload, options);
    }

    reload(property: string) {
        if (this.propertyName === 'localName') {
            this.route.navigateByUrl(this.toscaComponent.toscaType + '/'
                + encodeURIComponent(encodeURIComponent(this.toscaComponent.namespace)) + '/' + property);
        } else {
            this.route.navigateByUrl(this.toscaComponent.toscaType + '/'
                + encodeURIComponent(encodeURIComponent(property)) + '/' + this.toscaComponent.localName);
        }
    }
}
