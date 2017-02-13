import { Component, OnInit, Input } from '@angular/core';

@Component({
    selector: 'winery-instance-editXML',
    templateUrl: 'editXML.component.html'
})
export class EditXMLComponent implements OnInit {
     xmlData: string ;
     testdata: string;
    @Input() areaid: string;
    constructor() {
    }

    ngOnInit() {
        this.xmlData = '<?xml version="1.0" encoding="UTF-8"?><tosca:Definitions xmlns:tosca="http://docs.oasis-open.org/tosca/ns/2011/12" id="winery-devs-for_xmlns-TestName2" targetNamespace="http://www.example.org/namespace0"><tosca:NodeType name="TestName2" targetNamespace="http://www.example.org/namespace0"/></tosca:Definitions>';

        this.testdata = 'xmltest';
    }
}
