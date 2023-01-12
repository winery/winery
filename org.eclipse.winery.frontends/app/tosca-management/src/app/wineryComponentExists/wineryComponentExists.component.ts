/*******************************************************************************
 * Copyright (c) 2017-2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ExistService } from '../wineryUtils/existService';
import { ModalDirective } from 'ngx-bootstrap';
import { backendBaseURL } from '../configuration';
import { ToscaTypes } from '../model/enums';
import { WineryVersion } from '../model/wineryVersion';
import { ArtifactTypeSelectData } from '../model/selectData';
import {
    InterfaceOperationApiData, InterfacesApiData
} from '../instance/sharedComponents/interfaces/interfacesApiData';

@Component({
    selector: 'winery-component-exists',
    templateUrl: './wineryComponentExists.component.html',
})
export class WineryComponentExistsComponent implements OnInit, OnChanges {

    /**
     * This component is for checking whether a given component already exists in the repository and displays it
     * accordingly.
     * <label>Inputs</label>
     * <ul>
     *     <li><code>generateData</code> This input of type {@link GenerateData} is mandatory and must contain all
     * necessary
     *     information for generating the url, such as the resource and resource type.
     *     </li>
     *     <li><code>modalRef</code> The modalRef is optional. You can give the reference to the modal which this component
     * is a part of. The reference is used to update the values each time the modal is shown.
     *     </li>
     * </ul>
     */
    @Input() generateData: GenerateData;
    @Input() modalRef: ModalDirective;
    @Input() artifactTypes: ArtifactTypeSelectData[];
    artifactTemplate: String = ToscaTypes.ArtifactTemplate;
    selectedArtifactType: ArtifactTypeSelectData;
    implementAllOperations = new InterfaceOperationApiData('all');

    randomIdSuffix = Math.random();

    private version = new WineryVersion('', 1, 1);

    constructor(private existService: ExistService) {
    }

    ngOnInit() {
        if (this.modalRef) {
            this.modalRef.onShow.subscribe(() => this.checkImplementationExists());
        }
        if (this.generateData.toscaType === ToscaTypes.ArtifactTemplate) {
            for (const value of this.artifactTypes) {
                value.text = value.name;
            }
            this.implementAllOperations = new InterfaceOperationApiData(this.getInterfaceNameWithoutNS());
            if (!this.generateData.selectedOperation) {
                this.generateData.selectedOperation = this.implementAllOperations;
            }
            if (this.generateData.selectedInterface.operations.length === 1) {
                this.generateData.selectedOperation = this.generateData.selectedInterface.operations[0];
            }
            this.selectedArtifactType = this.artifactTypes[0];
            this.generateData.artifactTypeQName = this.selectedArtifactType.qName;
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.generateData && this.generateData.toscaType === ToscaTypes.ArtifactTemplate) {
            this.implementAllOperations = new InterfaceOperationApiData(this.getInterfaceNameWithoutNS());
        }
    }

    checkImplementationExists(): void {
        if (this.generateData && this.generateData.toscaType
            && this.generateData.name && this.generateData.name.length > 0) {
            this.generateData.url = backendBaseURL + '/'
                + this.generateData.toscaType + '/'
                + encodeURIComponent(encodeURIComponent(this.generateData.namespace)) + '/'
                + this.generateData.name;

            this.generateData.url += WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + this.version.toString();
            this.generateData.version = this.version;

            this.generateData.url += '/';
        }

        if (this.generateData && this.generateData.namespace && !this.generateData.namespace.endsWith('/')) {
            this.existService.check(this.generateData.url)
                .subscribe(
                    () => this.generateData.createComponent = false,
                    () => this.generateData.createComponent = true
                );
        }
    }

    typeSelected(type) {
        this.generateData.artifactTypeQName = this.artifactTypes.find(value => value.name === type.id).qName;
    }

    handleRadioButton(operation: InterfaceOperationApiData) {
        const searchValue = RegExp(this.generateData.selectedOperation.name + '-IA$');
        if (operation.name === this.implementAllOperations.name) {
            const interfaceNameWithoutNS: string = this.getInterfaceNameWithoutNS();
            this.generateData.name = this.generateData.name.replace(searchValue, interfaceNameWithoutNS + '-IA');
            this.generateData.selectedOperation = operation;
            return;
        }
        this.generateData.name = this.generateData.name.replace(searchValue, operation.name + '-IA');
        this.generateData.selectedOperation = operation;
    }

    private getInterfaceNameWithoutNS() {
        return this.generateData.selectedInterface.name
            .substr(this.generateData.selectedInterface.name.lastIndexOf('/') + 1);
    }
}

export class GenerateData {
    toscaType: ToscaTypes;
    createComponent: boolean;
    namespace: string;
    name: string;
    url: string;
    version: WineryVersion;
    artifactTypeQName: string;
    selectedInterface: InterfacesApiData;
    selectedOperation: InterfaceOperationApiData;
}
