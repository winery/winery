import { Injectable } from '@angular/core';

import { InstanceData } from './instanceData';

@Injectable()
export class InstanceService {

    constructor() {
    }

    getInstanceData(type: string): InstanceData {
        console.log('getting InstanceData for ' + type);
        return {id: 'component_1', namespace: 'http://example[dot]org/', name: 'Component 1', resourceType: 'nodeType'};
    }
}
