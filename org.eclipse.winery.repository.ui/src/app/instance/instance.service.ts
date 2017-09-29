/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs';
import { backendBaseURL } from '../configuration';
import { WineryInstance, WineryTopologyTemplate } from '../wineryInterfaces/wineryComponent';
import { ToscaComponent } from '../wineryInterfaces/toscaComponent';
import { ToscaTypes } from '../wineryInterfaces/enums';

@Injectable()
export class InstanceService {

    toscaComponent: ToscaComponent;
    topologyTemplate: WineryTopologyTemplate = null;
    path: string;

    constructor(private http: Http) {
    }

    /**
     * Get the submenu for the given resource type for displaying a component instance.
     * TODO: instead of string[], use objects which contain displayName and url fragment
     *
     * @returns string[] containing all menus for each resource type.
     */
    public getSubMenuByResource(): string[] {
        let subMenu: string[];

        switch (this.toscaComponent.toscaType) {
            case ToscaTypes.NodeType:
                subMenu = ['Visual Appearance', 'Instance States', 'Interfaces', 'Implementations',
                    'Requirement Definitions', 'Capability Definitions', 'Properties Definition',
                    'Inheritance', 'Documentation', 'XML'];
                break;
            case ToscaTypes.ServiceTemplate:
                subMenu = ['Topology Template', 'Plans', 'Selfservice Portal',
                    'Boundary Definitions', 'Tags', 'Documentation', 'XML'];
                break;
            case ToscaTypes.RelationshipType:
                subMenu = ['Visual Appearance', 'Instance States', 'Source Interfaces', 'Target Interfaces',
                    'Valid Sources and Targets', 'Implementations', 'Properties Definition',
                    'Inheritance', 'Documentation', 'XML'];
                break;
            case ToscaTypes.ArtifactType:
                subMenu = ['Properties Definition', 'Inheritance', 'Templates', 'Documentation', 'XML'];
                break;
            case ToscaTypes.ArtifactTemplate:
                subMenu = ['README', 'Files', 'Source', 'Properties', 'Documentation', 'XML'];
                break;
            case ToscaTypes.RequirementType:
                subMenu = ['Required Capability Type', 'Properties Definition', 'Inheritance', 'Documentation', 'XML'];
                break;
            case ToscaTypes.CapabilityType:
                subMenu = ['Properties Definition', 'Inheritance', 'Documentation', 'XML'];
                break;
            case ToscaTypes.NodeTypeImplementation:
                subMenu = ['Implementation Artifacts', 'Deployment Artifacts', 'Inheritance', 'Documentation', 'XML'];
                break;
            case ToscaTypes.RelationshipTypeImplementation:
                subMenu = ['Implementation Artifacts', 'Inheritance', 'Documentation', 'XML'];
                break;
            case ToscaTypes.PolicyType:
                subMenu = ['Language', 'Applies To', 'Properties Definition', 'Inheritance', 'Templates', 'Documentation', 'XML'];
                break;
            case ToscaTypes.PolicyTemplate:
                subMenu = ['Properties', 'Documentation', 'XML'];
                break;
            case ToscaTypes.Imports:
                subMenu = ['All Declared Elements Local Names', 'All Defined Types Local Names'];
                break;
            default: // assume Admin
                subMenu = ['Namespaces', 'Repository', 'Plan Languages', 'Plan Types', 'Constraint Types', 'Log'];
        }

        return subMenu;
    }

    /**
     * Set the shared data for the children. The path to the actual component is also generated.
     */
    public setSharedData(toscaComponent: ToscaComponent): void {
        this.toscaComponent = toscaComponent;
        // In order to have always the base path of this instance, create the path here
        // instead of getting it from the router, because there might be some child routes included.
        this.path = '/' + this.toscaComponent.toscaType + '/'
            + encodeURIComponent(encodeURIComponent(this.toscaComponent.namespace)) + '/'
            + this.toscaComponent.localName;

        if (this.toscaComponent.toscaType === ToscaTypes.ServiceTemplate) {
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
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });
        return this.http.get(backendBaseURL + this.path + '/', options)
            .map(res => res.json());
    }

    public getTopologyTemplate(): Observable<WineryTopologyTemplate> {
        const headers = new Headers({ 'Accept': 'application/json' });
        const options = new RequestOptions({ headers: headers });
        return this.http.get(backendBaseURL + this.path + '/topologytemplate/', options)
            .map(res => res.json());
    }
}
