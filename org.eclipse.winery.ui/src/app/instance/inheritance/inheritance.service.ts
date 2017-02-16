import { Injectable } from '@angular/core';
import { InheritanceData } from "./inheritanceData";
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http } from '@angular/http';
import { backendBaseUri } from '../../configuration';


@Injectable()
export class InheritanceService {

    constructor(private http: Http) {
    }

    getInheritanceData(path: string): Observable<InheritanceData> {
        let headers = new Headers({'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});

        if (path.indexOf('inheritance') === -1) {
            path += '/inheritance';
        }

        return this.http.get(backendBaseUri + decodeURIComponent(path), options)
            .map(res => res.json());
    }

    saveInheritanceData(inheritanceData: InheritanceData): Observable<any> {
        return null;
    }
}
