/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import { Component, OnInit } from '@angular/core';
import { VersionSliderService } from './version-slider.service';
import { Options } from '@angular-slider/ngx-slider/options';
import { WineryVersion } from '../../../../tosca-management/src/app/model/wineryVersion';
import { BackendService } from '../services/backend.service';
import { TopologyTemplateUtil } from '../models/topologyTemplateUtil';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { WineryActions } from '../redux/actions/winery.actions';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { EntityTypesModel } from '../models/entityTypesModel';

@Component({
    selector: 'winery-version-slider',
    templateUrl: './version-slider.component.html',
    styleUrls: ['./version-slider.component.css']
})
export class VersionSliderComponent implements OnInit {

    private static readonly LEGEND_CHAR_LIMIT = 15;

    entityTypes: EntityTypesModel;

    sliderValue: number;
    versions: WineryVersion[];
    options: Options = {
        showTicksValues: true,
        stepsArray: undefined,
        translate: VersionSliderComponent.hideValues
    };

    constructor(private versionSliderService: VersionSliderService,
                private backendService: BackendService,
                private ngRedux: NgRedux<IWineryState>,
                private rendererActions: TopologyRendererActions,
                private wineryActions: WineryActions,
                private configurationService: WineryRepositoryConfigurationService) {
        this.versionSliderService.getVersions()
            .subscribe(versions => this.init(versions));
        this.ngRedux.select(state => state.wineryState.entityTypes)
            .subscribe(data => {
                if (data) {
                    this.entityTypes = data;
                }
            });
    }

    ngOnInit() {
    }

    private init(versions: WineryVersion[]) {
        this.versions = versions;

        const id = this.backendService.configuration.id;
        this.sliderValue = this.versions
            .findIndex(v => this.toId(v) === id);

        const stepsArray = [];
        this.versions.forEach((version, index) => {
            const legend = VersionSliderComponent.limitChars(version.toReadableString());
            stepsArray.push({ value: index, legend });
        });
        // trigger change detection
        const newOptions: Options = Object.assign({}, this.options);
        newOptions.stepsArray = stepsArray;
        this.options = newOptions;
    }

    updateTopologyTemplate() {
        const version = this.getSelectedVersion();
        const id = this.toId(version);

        this.versionSliderService.getTopologyTemplate(id)
            .subscribe(topologyTemplate => {
                    TopologyTemplateUtil.updateTopologyTemplate(
                        this.ngRedux,
                        this.wineryActions,
                        topologyTemplate,
                        this.entityTypes,
                        this.configurationService.isYaml()
                    );
                }
            );
    }

    selectedIsCurrent(): boolean {
        return this.toId(this.getSelectedVersion())
            === this.backendService.configuration.id;
    }

    open() {
        const version = this.getSelectedVersion();
        const id = this.toId(version);
        let editorConfig = '?repositoryURL=' + encodeURIComponent(this.backendService.configuration.repositoryURL)
            + '&uiURL=' + encodeURIComponent(this.backendService.configuration.uiURL)
            + '&ns=' + encodeURIComponent(this.backendService.configuration.ns)
            + '&id=' + id;
        if (!version.editable) {
            editorConfig += '&isReadonly=true';
        }
        window.open(editorConfig, '_blank');
    }

    private getSelectedVersion() {
        return this.versions[this.sliderValue];
    }

    private toId(version: WineryVersion): string {
        return VersionSliderComponent.getName(this.backendService.configuration.id)
            + WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR
            + version.toString();
    }

    private static getName(id: string) {
        return id.split(WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR)[0];
    }

    private static hideValues(): string {
        return '';
    }

    private static limitChars(str: string): string {
        if (str.length < this.LEGEND_CHAR_LIMIT) {
            return str;
        } else {
            return str.substr(0, this.LEGEND_CHAR_LIMIT) + '...';
        }
    }
}
