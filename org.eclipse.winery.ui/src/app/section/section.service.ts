import { Injectable } from '@angular/core';

import { SectionData } from './sectionData';

@Injectable()
export class SectionService {

    constructor() {
    }

    getSectionData(type: string): SectionData[] {
        console.log('getting componentData for ' + type);
        return [
            {id: 'component_1', namespace: 'http://example[dot]org/', name: 'Component 1'},
            {id: 'component_20', namespace: 'http://example[dot]org/', name: 'Component 20'},
            {id: 'component_397', namespace: 'http://example[dot]org/', name: 'Component 397'},
            {id: 'component_873', namespace: 'http://example[dot]org/', name: 'Component 873'},
        ];
    }
}
