/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Philipp Meyer & Tino Stadelmaier - initial API and implementation
 *     Niko Stadelmaier - get path from url
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../../../configuration';

function escapeHtml (text: string): string {
     const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        '\'': '&#039;'
    };

    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}

@Injectable()
export class XMLEditorService {

    private path: string;

    constructor(private http: Http,
                private route: Router) {
        this.path = decodeURIComponent(this.route.url);
    }

    getXmlData(): Observable<string> {
        const headers = new Headers({ 'Accept': 'application/xml' });
        const options = new RequestOptions({ headers: headers });

        return this.http.get(backendBaseURL + this.path, options)
            .map(res => res.text());
    }

    saveXmlData(xmlData: String): Observable<any> {
        const headers = new Headers({ 'Content-Type': 'text/xml' });
        const options = new RequestOptions({ headers: headers });

        return this.http.put(backendBaseURL + this.path + '/', xmlData, options);
    }
}
