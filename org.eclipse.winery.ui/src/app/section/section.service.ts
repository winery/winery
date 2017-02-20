import { Injectable } from '@angular/core';
import { SectionData } from './sectionData';
import { Headers, RequestOptions, Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs';
import { backendBaseUri } from '../configuration';

@Injectable()
export class SectionService {

    constructor(private http: Http) {
    }

    getSectionData(type: string): Observable<SectionData[]> {
        let headers = new Headers({'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http.get(backendBaseUri + '/' + type.toLowerCase() + '/', options)
            .map(res => res.json());
    }
}
