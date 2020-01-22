/********************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import { Component, DoCheck, OnInit, ViewChild } from '@angular/core';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';
import { ModalDirective } from 'ngx-bootstrap';
import { isNullOrUndefined } from 'util';
import { HttpErrorResponse } from '@angular/common/http';
import { PlacementService } from './placementService';
import { AddComponentValidation } from '../../wineryAddComponentModule/addComponentValidation';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'winery-placement',
    templateUrl: 'placement.component.html',
    styleUrls: [
        'placement.component.css'
    ],
    providers: [
        PlacementService
    ]
})
export class PlacementComponent implements DoCheck, OnInit {

    readonly defaultNS = 'http://opentosca.org/servicetemplates';

    isModalShown = false;
    isFormValid = false;
    openInTab = true;
    dataFlow = '';
    validation: AddComponentValidation;
    newSTemplateName: string;
    newSTemplateNamespace: string;
    dataFlowXML: Document;
    changed = false;

    file: File;

    @ViewChild('createFromDataFlowModal') createFromDataFlowModal: ModalDirective;
    @ViewChild('componentName') componentName: FormsModule;
    @ViewChild('componentNamespace') componentNamespace: FormsModule;

    constructor(private service: PlacementService,
                private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        this.newSTemplateNamespace = this.defaultNS;
    }

    ngDoCheck(): void {
        if (!isNullOrUndefined(this.file) && (this.file.name.endsWith('.xml') || this.file.name.endsWith('.txt'))) {
            this.isFormValid = true;
        } else {
            this.isFormValid = false;
        }
    }

    onAddClick() {
        // set final id and final namespace of the data flow model.
        this.dataFlowXML.documentElement.setAttribute('xmlns:new_st_ns', this.newSTemplateNamespace);
        this.dataFlowXML.documentElement.setAttribute('id', 'new_st_ns:' + this.newSTemplateName);

        this.dataFlow = new XMLSerializer().serializeToString(this.dataFlowXML);
        this.service.createTemplateFromDataFlow(this.dataFlow).subscribe(
            data => {
                const templateURL = data.headers.get('Location');
                this.notify.success(templateURL, 'Service Template successfully created!');
                if (this.openInTab) {
                    window.open(templateURL + '/topologytemplate', '_blank');
                }
            },
            error => this.handleError(error)
        );

        this.resetArtifactCreationData();
        this.createFromDataFlowModal.hide();
    }

    public onCreateFromDataFlow() {
        this.isModalShown = true;
    }

    public fileChange(event: any) {
        const fileList: FileList = event.target.files;
        if (fileList.length > 0) {
            this.file = fileList[0];
        }

        // get id and namespace of the selected data flow model if specified.
        const fileReader = new FileReader();
        fileReader.readAsText(this.file);

        fileReader.onload = (e) => {
            this.dataFlow = fileReader.result.toString();

            const parser = new DOMParser();
            this.dataFlowXML = parser.parseFromString(this.dataFlow, 'text/xml');
            const id = this.dataFlowXML.documentElement.getAttribute('id');

            let ns;
            let nsPrefix;
            if (id.includes(':')) {
                nsPrefix = id.substring(0, id.indexOf(':'));
                this.newSTemplateName = id.substring(id.indexOf(':') + 1);
                ns = this.dataFlowXML.documentElement.lookupNamespaceURI(nsPrefix);
            } else {
                this.newSTemplateName = id;
            }
            if (ns) {
                this.newSTemplateNamespace = ns;
            }
        };
    }

    public onHidden(): void {
        this.isModalShown = false;
    }

    public hideCreateFromDataFlowModal() {
        this.resetArtifactCreationData();
        this.createFromDataFlowModal.hide();
    }

    public resetArtifactCreationData() {
        this.file = null;
    }

    private handleError(error: HttpErrorResponse) {
        this.notify.error(error.message);
    }

    toggleEditable(event: { target: { checked: any; }; }) {
        this.openInTab = event.target.checked;
    }
}


