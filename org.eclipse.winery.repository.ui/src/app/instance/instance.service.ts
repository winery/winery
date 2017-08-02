/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 *     Niko Stadelmaier - add admin component
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import { isNullOrUndefined } from 'util';
import { backendBaseURL } from '../configuration';
import { WineryInstance, WineryTopologyTemplate } from '../wineryInterfaces/wineryComponent';

@Injectable()
export class InstanceService {

    selectedResource: string;
    selectedComponentId: string;
    selectedNamespace: string;
    topologyTemplate: WineryTopologyTemplate = null;
    path: string;

    constructor(private http: Http) {
    }

    /**
     * Get the submenu for the given resource type for displaying a component instance.
     *
     * @param type specifies the resource type for this particular instance.
     * @returns string[] containing all menus for each resource type.
     */
    public getSubMenuByResource(type?: string): string[] {
        if (isNullOrUndefined(type)) {
            type = this.selectedResource;
        }

        let subMenu: string[];

        switch (type.toLowerCase()) {
            case 'nodetype':
                subMenu = ['Visual Appearance', 'Instance States', 'Interfaces', 'Implementations',
                    'Requirement Definitions', 'Capability Definitions', 'Properties Definition',
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
                subMenu = ['Language', 'Applies To', 'Properties Definition', 'Inheritance', 'Documentation', 'XML'];
                break;
            case 'policytemplate':
                subMenu = ['Properties', 'Documentation', 'XML'];
                break;
            case 'xsdimport':
                subMenu = [''];
                break;
            case 'wsdlimport':
                subMenu = [''];
                break;
            case 'admin':
                subMenu = ['Namespaces', 'Repository', 'Plan Languages', 'Plan Types', 'Constraint Types', 'Log'];
                break;
            default:
                subMenu = [''];
        }

        return subMenu;
    }

    /**
     * Set the shared data for the children. The path to the actual component is also generated.
     *
     * @param selectedResource
     * @param selectedNamespace
     * @param selectedComponentId
     */
    public setSharedData(selectedResource: string, selectedNamespace: string, selectedComponentId: string): void {
        this.selectedNamespace = selectedNamespace;
        this.selectedComponentId = selectedComponentId;
        this.selectedResource = selectedResource;
        // In order to have always the base path of this instance, create the path here
        // instead of getting it from the router, because there might be some child routes included.
        this.path = '/' + this.selectedResource.toLowerCase() + 's/'
            + encodeURIComponent(encodeURIComponent(this.selectedNamespace)) + '/'
            + this.selectedComponentId;

        if (this.selectedResource === 'serviceTemplate') {
            this.getTopologyTemplate()
                .subscribe(
                    data => this.topologyTemplate = data,
                    error => this.topologyTemplate = null
                );
        }
    }

    public deleteComponent(): Observable<any> {
        return this.http.delete(backendBaseURL + this.path + '/');
    }

    public getComponentData(): Observable<WineryInstance> {
        const headers = new Headers({'Content-Type': 'application/xml'});
        const options = new RequestOptions({headers: headers});
        return this.http.get(backendBaseURL + this.path + '/', options)
            .map(res => res.json());
    }

    private getTopologyTemplate(): Observable<WineryTopologyTemplate> {
        const headers = new Headers({'Content-Type': 'application/json'});
        const options = new RequestOptions({headers: headers});
        return this.http.get(backendBaseURL + this.path + '/topologytemplate/', options)
            .map(res => res.json());
    }
}
