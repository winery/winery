import { Injectable } from '@angular/core';
import { InheritanceData } from "./inheritanceData";


@Injectable()
export class InheritanceService {

    constructor() {
    }

    getInheritanceData(): InheritanceData {
        // console.log('getting componentData for ' + type);
        return {
            abstract: true,
            final: false,
            derivedFrom: [
                {name: 'test1', QName: '{http://example.org}test1', selected: false},
                {name: 'test2', QName: '{http://example.org}test2', selected: true},
                {name: 'test3', QName: '{http://example.org}test3', selected: false},
                {name: 'test4', QName: '{http://example.org}test4', selected: false},
                {name: 'test5', QName: '{http://example.org}test5', selected: false}
            ]
        };

    }
}
