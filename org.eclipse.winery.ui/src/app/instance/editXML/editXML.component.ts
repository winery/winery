
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
import { WindowRefService } from './windowRef.service';


@Component({
    selector: 'winery-instance-editXML',
    templateUrl: 'editXML.component.html',
    providers: [EditXMLService, WindowRefService ],
})
export class EditXMLComponent implements OnInit {
    id: string = 'XML';
    styleAttr: any;
    dataEditorLang: any;

    // Set height to 300 px
    height = 300;

    loading: boolean = true;
    xmlData: string;
    window: any;


    constructor(
        private sharedData: InstanceService,
        private service: EditXMLService,
        private windowService : WindowRefService,
    ) {
        this.dataEditorLang = 'application/xml';
        // this.styleAttr = null;
    }


    ngOnInit() {

        this.window = this.windowService.nativeWindow;

        this.service.setPath(this.sharedData.path);

        this.service.getXmlData()
            .subscribe(
                data => this.handleXmlData(data),
                error => this.handleError(error)
            );
    }

    private handleXmlData(xml: string) {
        this.xmlData = xml;

        if (!isNullOrUndefined(this.xmlData)) {
            this.loading = false;
        }
    }

    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }

     saveXmlData(): void {

        console.log("save button clicked");

        this.service.saveXmlData(this.window.winery.orionareas.xml.editor[0].getText())
            .subscribe (
                data => this.handlePutResponse(data),
                error => this.handleError(error)
        );
    }

    private handlePutResponse(response: any) {
        this.loading = false;
        console.log(response);
    }
}
