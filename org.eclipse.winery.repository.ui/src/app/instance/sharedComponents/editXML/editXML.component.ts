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
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { EditXMLService } from './editXML.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryEditorComponent } from '../../../wineryEditorModule/wineryEditor.component';
import { InstanceService } from '../../instance.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

declare var requirejs: any;

@Component({
    selector: 'winery-instance-edit-xml',
    templateUrl: 'editXML.component.html',
    providers: [EditXMLService]
})
export class EditXMLComponent implements OnInit {

    @Input() getXmlData = true;
    @Input() hideSaveButton = false;
    @Input() xmlData: string;

    @ViewChild('editor') editor: WineryEditorComponent;
    loading = true;

    id = 'XML';
    dataEditorLang = 'application/xml';

    // Set height to 500 px
    height = 500;

    constructor(public sharedData: InstanceService,
                private service: EditXMLService,
                private notify: WineryNotificationService) {
    }

    ngOnInit() {
        if (this.getXmlData) {
            this.service.getXmlData()
                .subscribe(
                    data => this.handleXmlData(data),
                    error => this.handleError(error)
                );
        } else {
            this.loading = false;
        }
    }

    saveXmlData(): void {
        this.service.saveXmlData(this.editor.getData())
            .subscribe(
                data => this.handlePutResponse(data),
                error => this.handleError(error)
            );
        this.loading = true;
    }

    getEditorContent(): string {
        return this.editor.getData();
    }

    setEditorContent(xml: string) {
        this.xmlData = xml;
        this.editor.setData(this.xmlData);
    }

    private handleXmlData(xml: string) {
        this.loading = false;
        this.xmlData = xml;
    }

    private handleError(error: HttpErrorResponse): void {
        this.loading = false;
        this.notify.error(error.message);
    }

    private handlePutResponse(response: HttpResponse<string>) {
        this.loading = false;
        this.notify.success('Successfully saved data!');

        if (response.body) {
            this.notify.warning(response.body);
        }
    }

}
