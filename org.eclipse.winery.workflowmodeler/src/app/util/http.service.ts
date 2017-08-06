/**
 * Copyright (c) 2017 ZTE Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     ZTE - initial API and implementation and/or initial documentation
 */
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
