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

    public getSubMenuByResource(type: string): string[] {
        let subMenu: string[];

        switch (type.toLowerCase()) {
            case 'nodetype':
                subMenu = ['Visual Appearance', 'Instance States', 'Interfaces', 'Implementations',
                    'Requirement Definitions' , 'Capability Definitions', 'Property Definition',
                    'Inheritance', 'Documentation', 'XML'];
                break;
            case 'servicetemplate':
                subMenu = ['Topology Template', 'Plans', 'Selfservice Portal',
                    'Boundary Definitions', 'Tags', 'Documentation', 'XML'];
                break;
            case 'relationshiptype':
                subMenu = ['Visual Appearance', 'Instance States', 'Source Interfaces', 'Target Interfaces',
                    'Valid Sources and Targets', 'Implementations', 'Property Definition',
                    'Inheritance', 'Documentation', 'XML'];
                break;
            //TODO: add all;
            default:
                subMenu = [''];
        }

        return subMenu;
    }
}
