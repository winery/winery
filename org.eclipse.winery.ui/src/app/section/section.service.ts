import { Injectable } from '@angular/core';

import { SectionData } from './sectionData';

@Injectable()
export class SectionService {

    constructor() {
    }

    getSectionData(type: string): SectionData[] {
        console.log('getting componentData for ' + type);
        return [
            { id: 'Apache-2.4', namespace: 'http://opentosca.org/nodetypes' },
            { id: 'Component.1', namespace: 'http://example.org/' },
            { id: 'Component_20', namespace: 'http://example[dot]org/' },
            { id: 'Component_397', namespace: 'http://example[dot]org/' },
            { id: 'Component_873', namespace: 'http://example[dot]org/' },
        ];
    }
}
