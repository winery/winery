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
 */
import { Component, OnInit, Input, ElementRef } from '@angular/core';
import { EditXMLService } from './editXML.service';
import { NotificationService } from '../../notificationModule/notification.service';

declare var requirejs: any;

@Component({
    selector: 'winery-instance-editXML',
    templateUrl: 'editXML.component.html',
    providers: [EditXMLService]
})
export class EditXMLComponent implements OnInit {

    id = 'XML';
    dataEditorLang = 'application/xml';

    // Set height to 500 px
    height = 500;

    loading = true;
    xmlData: string;
    orionEditor: any = undefined;


    constructor(private service: EditXMLService, private notify: NotificationService) {
    }


    ngOnInit() {
        Promise.all([
            require('../../../static/orion-editor.min.js'),
            require('../../../css/orion-editor.css')
        ]).then(function() {
            requirejs(['orion/editor/edit'], function(edit: any) {
                this.orionEditor = edit({className: 'editor', parent: 'xml'})[0];
                this.receiveXmlData();
            }.bind(this));
        }.bind(this));
    }

    saveXmlData(): void {
        this.service.saveXmlData(this.orionEditor.getText())
            .subscribe (
                data => this.handlePutResponse(data),
                error => this.handleError(error)
            );
        this.loading = true;
    }

    private receiveXmlData() {
        this.service.getXmlData()
            .subscribe(
                data => this.handleXmlData(data),
                error => this.handleError(error)
            );
    }

    private handleXmlData(xml: string) {
        this.loading = false;
        this.orionEditor.setText(xml);
    }

    private handleError(error: any): void {
        this.loading = false;
        this.notify.error(error.toString());
    }

    private handlePutResponse(response: any) {
        this.loading = false;
        this.notify.success('Successfully saved data!');
    }
}
