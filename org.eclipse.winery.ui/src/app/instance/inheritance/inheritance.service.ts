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

        return this.http.get(backendBaseUri + path, options)
            .map(res => res.json());


       /* return {
            abstract: true,
            final: false,
            derivedFrom: [
                {name: 'test1', QName: '{http://example.org}test1', selected: false},
                {name: 'test2', QName: '{http://example.org}test2', selected: true},
                {name: 'test3', QName: '{http://example.org}test3', selected: false},
                {name: 'test4', QName: '{http://example.org}test4', selected: false},
                {name: 'test5', QName: '{http://example.org}test5', selected: false}
            ]
        };*/
    }

    saveInheritanceData(inheritanceData: InheritanceData): Observable<any> {
        return null;
    }
}
