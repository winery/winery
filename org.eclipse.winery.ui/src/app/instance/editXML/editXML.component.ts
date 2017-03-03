
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
import { InstanceService } from '../instance.service';
import { isNullOrUndefined } from 'util';
import { EditXMLService } from './editXML.service';

declare var requirejs: any;

@Component({
    selector: 'winery-instance-editXML',
    templateUrl: 'editXML.component.html',
    providers: [EditXMLService]
})
export class EditXMLComponent implements OnInit {
    id: string = 'XML';
    dataEditorLang: any;

    // Set height to 500 px
    height = 500;

    loading: boolean = true;
    xmlData: string;
    orioneditor: any = undefined;


    constructor(
        private sharedData: InstanceService,
        private service: EditXMLService,
    ) {
        this.dataEditorLang = 'application/xml';
    }


    ngOnInit() {
        Promise.all([
            require('http://www.eclipse.org/orion/editor/releases/current/built-editor.min.js'),
            require('http://eclipse.org/orion/editor/releases/current/built-editor.css')
        ]).then(function() {
            requirejs(['orion/editor/edit'], function(edit: any) {
                this.orioneditor = edit({className: 'editor', parent: 'xml'})[0];
                this.receiveXmlData();
            }.bind(this));
        }.bind(this));

        this.service.setPath(this.sharedData.path);
    }

    saveXmlData(): void {
        this.service.saveXmlData(this.orioneditor.getText())
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
        this.orioneditor.setText(xml);
    }

    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }

    private handlePutResponse(response: any) {
        this.loading = false;
        console.log(response);
    }
}
