import { Injectable } from '@angular/core';
import { SectionData } from './sectionData';
import { Headers, RequestOptions, Http } from '@angular/http';
import 'rxjs/add/operator/map';
import { Observable } from 'rxjs';

@Injectable()
export class SectionService {

    constructor(private http: Http) {
    }

    getSectionData(type: string): Observable<SectionData[]> {
        console.log('getting components for ' + type);

        let headers = new Headers({'Accept': 'application/json'});
        let options = new RequestOptions({headers: headers});

        return this.http.get('http://127.0.0.1:8080/winery/' + type.toLowerCase(), options)
            .map(res => res.json());
    }
}
