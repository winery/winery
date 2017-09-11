/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Tino Stadelmaier, Philipp Meyer - initial API and implementation
 *     Lukas Harzenetter - HTTP message in notification
 */
import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Response } from '@angular/http';
import { EditXMLService } from './editXML.service';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { WineryEditorComponent } from '../../../wineryEditorModule/wineryEditor.component';

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

    constructor(private service: EditXMLService,
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
                (error: Response) => this.handleError(error.text())
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

    private handleError(error: string): void {
        this.loading = false;
        this.notify.error(error);
    }

    private handlePutResponse(response: Response) {
        this.loading = false;
        this.notify.success('Successfully saved data!');

        if (response.text()) {
            this.notify.warning(response.text());
        }
    }

}
