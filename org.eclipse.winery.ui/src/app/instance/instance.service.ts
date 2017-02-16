import { Injectable } from '@angular/core';

import { InstanceData } from './instanceData';
import { isNullOrUndefined } from 'util';

@Injectable()
export class InstanceService {

    selectedResource: string;
    selectedComponentId: string;
    selectedNamespace: string;
    path: string;

    constructor() {}

    public getSubMenuByResource(type?: string): string[] {
        if (isNullOrUndefined(type)) {
            type = this.selectedResource;
        }

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

    public setSharedData(selectedResource: string, selectedComponentId: string, selectedNamespace: string, path: string): void {
        this.selectedNamespace = selectedNamespace;
        this.selectedComponentId = selectedComponentId;
        this.selectedResource = selectedResource;
        this.path = path;
    }
}
