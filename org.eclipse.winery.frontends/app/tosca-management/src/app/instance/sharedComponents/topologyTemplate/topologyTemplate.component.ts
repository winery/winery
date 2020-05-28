/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
import { Component, OnInit, ViewChild } from '@angular/core';
import { InstanceService } from '../../instance.service';
import { backendBaseURL, editorURL } from '../../../configuration';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { WineryVersion } from '../../../model/wineryVersion';
import { BsModalRef, BsModalService, ModalDirective } from 'ngx-bootstrap';
import { ActivatedRoute } from '@angular/router';
import { ToscaTypes } from '../../../model/enums';
import { WineryRepositoryConfigurationService } from '../../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';

@Component({
    templateUrl: 'topologyTemplate.component.html',
})
export class TopologyTemplateComponent implements OnInit {

    readonly uiURL = encodeURIComponent(window.location.origin + window.location.pathname + '#/');

    loading = true;
    templateUrl: SafeResourceUrl;
    editorUrl: string;
    refinementAvailable = false;

    selectedVersion: WineryVersion;

    @ViewChild('compareToModal') compareToModal: ModalDirective;
    compareToModalRef: BsModalRef;

    constructor(private sanitizer: DomSanitizer,
                public sharedData: InstanceService,
                private modalService: BsModalService,
                private activatedRoute: ActivatedRoute,
                private configurationService: WineryRepositoryConfigurationService) {
    }

    ngOnInit() {
        this.templateUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
            this.sharedData.path + '/topologytemplate/?view&uiURL=' + this.uiURL
        );

        let editorConfig = '?repositoryURL=' + encodeURIComponent(backendBaseURL)
            + '&uiURL=' + this.uiURL
            + '&ns=' + encodeURIComponent(this.sharedData.toscaComponent.namespace)
            + '&id=' + this.sharedData.toscaComponent.localName;

        // for declarative compliance rules add additional information to identify the location of the topology template
        if (this.sharedData.toscaComponent.toscaType !== ToscaTypes.ServiceTemplate) {
            const elementPath = this.activatedRoute.snapshot.url[0].path;
            editorConfig += '&parentPath=' + this.sharedData.toscaComponent.toscaType
                + '&elementPath=' + elementPath;
        }

        if (!this.sharedData.currentVersion.editable) {
            editorConfig += '&isReadonly=true';
        }

        // Disabled next line since in most use cases we don't run the Topology Modeler in a separate instance
        // this.editorUrl = this.configurationService.configuration.endpoints.topologymodeler + editorConfig;
        this.editorUrl = editorURL + editorConfig;
        this.refinementAvailable = this.sharedData.toscaComponent.toscaType === ToscaTypes.ServiceTemplate;
    }

    versionSelected(version: WineryVersion) {
        this.selectedVersion = version;
    }

    onCompare() {
        let compareUrl = this.editorUrl + '&compareTo='
            + this.sharedData.toscaComponent.localNameWithoutVersion;

        if (this.selectedVersion.toString().length > 0) {
            compareUrl += WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR
                + this.selectedVersion.toString();
        }

        window.open(compareUrl, '_blank');
    }

    showCompareToModal() {
        this.compareToModalRef = this.modalService.show(this.compareToModal);
    }
}
