import { Injectable } from '@angular/core';

import { InstanceData } from './instanceData';

@Injectable()
export class InstanceService {

    constructor() {
    }

    public getSubMenuByResource(type: string): string[] {
        let subMenu: string[];

        switch (type.toLowerCase()) {
            case 'nodetype':
                subMenu = ['Visual Appearance', 'Instance States', 'Interfaces', 'Implementations',
                    'Requirement Definitions' , 'Capability Definitions', 'Properties Definition',
                    'Inheritance', 'Documentation', 'XML'];
                break;
            case 'servicetemplate':
                subMenu = ['Topology Template', 'Plans', 'Selfservice Portal',
                    'Boundary Definitions', 'Tags', 'Documentation', 'XML'];
                break;
            case 'relationshiptype':
                subMenu = ['Visual Appearance', 'Instance States', 'Source Interfaces', 'Target Interfaces',
                    'Valid Sources and Targets', 'Implementations', 'Properties Definition',
                    'Inheritance', 'Documentation', 'XML'];
                break;
            case 'artifacttype':
                subMenu = ['Properties Definition', 'Inheritance', 'Documentation', 'XML'];
                break;
            case 'artifacttemplate':
                subMenu = ['Files', 'Properties', 'Documentation', 'XML'];
                break;
            case 'requirementtype':
                subMenu = ['Required Capability Type', 'Properties Definition', 'Inheritance', 'Documentation', 'XML'];
                break;
            case 'capabilitytype':
                subMenu = ['Properties Definition', 'Inheritance', 'Documentation', 'XML'];
                break;
            case 'nodetypeimplementation':
                subMenu = ['Implementation Artifacts', 'Deployment Artifacts', 'Inheritance', 'Documentation', 'XML'];
                break;
            case 'relationshiptypeimplementation':
                subMenu = ['Implementation Artifacts', 'Inheritance', 'Documentation', 'XML'];
                break;
            case 'policytype':
                subMenu = [''];
                break;
            case 'policytemplate':
                subMenu = [''];
                break;
            case 'xsdimport':
                subMenu = [''];
                break;
            case 'wsdlimport':
                subMenu = [''];
                break;
            default:
                subMenu = [''];
        }

        return subMenu;
    }
}
