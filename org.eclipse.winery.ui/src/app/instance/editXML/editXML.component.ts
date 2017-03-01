
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
import { QNameList } from '../../qNameSelector/qNameApiData';
import { isNullOrUndefined } from 'util';
import { EditXMLService } from './editXML.service';

@Component({
    selector: 'winery-instance-editXML',
    templateUrl: 'editXML.component.html',
    providers: [EditXMLService],
})
export class EditXMLComponent implements OnInit {
    xmlData2: string ;
    id: string = 'id';
    styleAttr: any;
    dataEditorLang: any;

    // Set height to 300 px
    height = 300;

    loading: boolean = true;
    xmlData: string;



    constructor(
        private sharedData: InstanceService,
        private service: EditXMLService,
    ) {
        this.dataEditorLang = 'application/xml';
        // this.styleAttr = null;
    }


    ngOnInit() {

        this.service.setPath(this.sharedData.path);
        this.service.getXmlData()
            .subscribe(
                data => this.handleXmlData(data),
                error => this.handleError(error)
            );

        this.xmlData2 = `
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<tosca:Definitions id="winery-defs-for_ns5-ChangeName" targetNamespace="http://www.w3.org/XML/1998/namespace3" xmlns:tosca="http://docs.oasis-open.org/tosca/ns/2011/12" xmlns:ns0="http://www.eclipse.org/winery/model/selfservice" xmlns:winery="http://www.opentosca.org/winery/extensions/tosca/2013/02/12">
    <tosca:NodeType name="ChangeName" targetNamespace="http://www.w3.org/XML/1998/namespace2" winery:bordercolor="#8cb215">
        <winery:PropertiesDefinition elementname="Properties" namespace="http://www.w3.org/XML/1998/namespace/propertiesdefinition/winery">
            <winery:properties>
                <winery:key>TestProperty</winery:key>
                <winery:type>xsd:string</winery:type>
            </winery:properties>
        </winery:PropertiesDefinition>
    </tosca:NodeType>
</tosca:Definitions>
`;

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
        this.service.saveXmlData(this.xmlData)
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
