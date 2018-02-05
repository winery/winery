/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { Http, RequestOptionsArgs } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import './rxjs-operators';

@Injectable()
export class HttpService {
    constructor(private http: Http) {}

    public get(uri: string): Observable<any> {
        return this.getHttp('get', uri);
    }

    public post(uri: string, data: any): Observable<any> {
        return this.getHttp('post', uri, data);
    }

    public put(uri: string, data: any, options?: RequestOptionsArgs): Observable<any> {
        return this.getHttp('put', uri, data, options);
    }

    public delete(uri: string): Observable<any> {
        return this.getHttp('delete', uri);
    }

    public getHttp(type: string, uri: string, data?: any, options?: RequestOptionsArgs): Observable<any> {
        if (data) {
            return this.http[type](uri, data, options)
                .map(response => response.json())
                .catch(this.handleError);
        } else {
            return this.http[type](uri, options)
                .map(response => response.json())
                .catch(this.handleError);
        }
    }

    private handleError(error: any) {
        const errMsg = (error.message) ? error.message :
            error.status ? `${error.status}-${error.statusText}` : 'Server error';
        return Observable.throw(errMsg);
    }
}
