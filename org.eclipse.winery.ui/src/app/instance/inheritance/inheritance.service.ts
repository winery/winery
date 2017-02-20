import { Injectable } from '@angular/core';
import { InheritanceData } from './inheritanceData';
import { Observable } from 'rxjs';
import { Headers, RequestOptions, Http } from '@angular/http';
import { backendBaseUri } from '../../configuration';

@Injectable()
export class InheritanceService {

    private path: string;

    constructor(private http: Http) {
    }

    getInheritanceData(path: string): Observable<InheritanceData> {
        let headers = new Headers({ 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        if (path.indexOf('inheritance') === -1) {
            path += '/inheritance/';
        } else {
            path += '/';
        }

        this.path = path;

        return this.http.get(backendBaseUri + decodeURIComponent(path), options)
            .map(res => res.json());
    }

    saveInheritanceData(inheritanceData: InheritanceData): Observable<any> {
        let headers = new Headers({ 'Content-Type': 'application/json', 'Accept': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        // create a copy to not send unnecessary data to the server
        let copy = new InheritanceData();
        copy.derivedFrom = inheritanceData.derivedFrom;
        copy.isAbstract = inheritanceData.isAbstract;
        copy.isFinal = inheritanceData.isFinal;

        return this.http.put(backendBaseUri + decodeURIComponent(this.path), JSON.stringify(copy), options);
    }
}
