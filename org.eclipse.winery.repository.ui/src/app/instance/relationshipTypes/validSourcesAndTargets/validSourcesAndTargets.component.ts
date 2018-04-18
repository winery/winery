/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { ValidService } from './validSourcesAndTargets.service';
import { ValidEndingsApiDataSet, ValidEndingsData, ValidEndingsSelectionEnum } from './validEndingsApiData';
import { SelectData } from '../../../wineryInterfaces/selectData';
import { isNullOrUndefined } from 'util';
import { SelectItem } from 'ng2-select';
import { InstanceService } from '../../instance.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
    selector: 'winery-valid-endings',
    templateUrl: 'validSourcesAndTargets.component.html',
    providers: [ValidService],
})
export class ValidSourcesAndTargetsComponent implements OnInit {
    // loading
    loading = true;
    loadingSrc = true;
    loadingTrg = true;

    // for radio button management
    selectedEnum = ValidEndingsSelectionEnum;

    // ValidEndings Data and SelectData
    validEndingsData: ValidEndingsData = new ValidEndingsData();
    selectSources: SelectData[] = null;
    selectTargets: SelectData[] = null;

    constructor(private service: ValidService,
                private notify: WineryNotificationService,
                public sharedData: InstanceService) {
    }

    ngOnInit() {
        this.loading = true;
        this.service.getValidEndingsData()
            .subscribe(
                data => this.handleValidEndingsData(data),
                error => this.handleError(error)
            );
    }

    private handleValidEndingsData(validEndingsData: ValidEndingsData) {
        this.loading = false;
        this.validEndingsData = validEndingsData;

        if (isNullOrUndefined(this.validEndingsData.validSource)) {
            this.validEndingsData.validSource = new ValidEndingsApiDataSet();
        } else {
            // convert validEndingsSelectionType to angular specific enum
            switch (this.validEndingsData.validSource.validEndingsSelectionType) {
                case this.selectedEnum.NODETYPE:
                    this.loadingSrc = true;
                    this.getSelectionData('/nodetypes?grouped=angularSelect', 'source');
                    break;
                case this.selectedEnum.REQTYPE:
                    this.loadingSrc = true;
                    this.getSelectionData('/requirementtypes?grouped=angularSelect', 'source');
                    break;
            }
        }

        if (isNullOrUndefined(this.validEndingsData.validTarget)) {
            this.validEndingsData.validTarget = new ValidEndingsApiDataSet();
        } else {
            // convert validEndingsSelectionType to angular specific enum
            switch (this.validEndingsData.validTarget.validEndingsSelectionType) {
                case this.selectedEnum.NODETYPE:
                    this.loadingTrg = true;
                    this.getSelectionData('/nodetypes?grouped=angularSelect', 'target');
                    break;
                case this.selectedEnum.CAPTYPE:
                    this.loadingTrg = true;
                    this.getSelectionData('/capabilitytypes?grouped=angularSelect', 'target');
                    break;
            }
        }
    }

    public onSelectedTrgValueChanged(event: SelectItem) {
        this.validEndingsData.validTarget.validDataSet = { id: event.id, text: event.text };
    }

    public onSelectedSrcValueChanged(event: SelectItem) {
        this.validEndingsData.validSource.validDataSet = { id: event.id, text: event.text };
    }

    public onValidSourceSelected(event: ValidEndingsSelectionEnum) {
        switch (event) {
            case ValidEndingsSelectionEnum.NODETYPE:
                this.validEndingsData.validSource.validEndingsSelectionType = this.selectedEnum.NODETYPE;
                this.getSelectionData('/nodetypes?grouped=angularSelect', 'source');
                break;
            case ValidEndingsSelectionEnum.REQTYPE:
                this.validEndingsData.validSource.validEndingsSelectionType = this.selectedEnum.REQTYPE;
                break;
            default:
                this.validEndingsData.validSource.validEndingsSelectionType = this.selectedEnum.EVERYTHING;
        }
    }

    public onValidTargetSelected(event: ValidEndingsSelectionEnum) {
        switch (event) {
            case ValidEndingsSelectionEnum.CAPTYPE:
                this.validEndingsData.validTarget.validEndingsSelectionType = this.selectedEnum.CAPTYPE;
                break;
            case ValidEndingsSelectionEnum.NODETYPE:
                this.validEndingsData.validTarget.validEndingsSelectionType = this.selectedEnum.NODETYPE;
                this.getSelectionData('/nodetypes?grouped=angularSelect', 'target');
                break;
            default:
                this.validEndingsData.validTarget.validEndingsSelectionType = this.selectedEnum.EVERYTHING;
        }
    }

    public saveToServer() {
        this.loading = true;

        if (this.validEndingsData.validSource.validEndingsSelectionType === this.selectedEnum.EVERYTHING ||
            this.validEndingsData.validSource.validEndingsSelectionType === this.selectedEnum.REQTYPE) {
            this.validEndingsData.validSource = null;
        }
        if (this.validEndingsData.validTarget.validEndingsSelectionType === this.selectedEnum.EVERYTHING ||
            this.validEndingsData.validTarget.validEndingsSelectionType === this.selectedEnum.CAPTYPE) {
            this.validEndingsData.validTarget = null;
        }

        this.service.saveValidEndings(this.validEndingsData)
            .subscribe(
                data => this.handleResponse(),
                error => this.handleError(error)
            );

        if (this.validEndingsData.validTarget === null) {
            this.validEndingsData.validTarget = new ValidEndingsApiDataSet();
        }
        if (this.validEndingsData.validSource === null) {
            this.validEndingsData.validSource = new ValidEndingsApiDataSet();
        }
    }

    private getSelectionData(reqDataPath: string, type: string) {
        this.service.getSelectorData(reqDataPath)
            .subscribe(
                data => {
                    if (type === 'source') {
                        this.selectSources = this.handleTypes(data);
                        this.loadingSrc = false;
                    } else if (type === 'target') {
                        this.selectTargets = this.handleTypes(data);
                        this.loadingTrg = false;
                    }
                },
                error => this.handleError(error)
            );
    }

    private handleTypes(types: SelectData[]): SelectData[] {
        return types.length > 0 ? types : null;
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message);
    }

    private handleResponse() {
        this.loading = false;
        this.notify.success('Successfully saved Valid Sources and Targets!');
    }
}
