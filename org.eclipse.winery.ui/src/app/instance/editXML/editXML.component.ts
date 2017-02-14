import { Component, OnInit, Input } from '@angular/core';

@Component({
    selector: 'winery-instance-editXML',
    templateUrl: 'editXML.component.html'
})
export class EditXMLComponent implements OnInit {
     xmlData: string ;
     testdata: string;
    @Input() areaid: string;
    id: string = 'id';

    constructor() {
    }

    ngOnInit() {
        this.xmlData = `
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

        this.testdata = `
/*
 * This is an Orion editor sample.
 */
function() {
    var a = 'hi there!';
    window.console.log(a);
}
`;
    }
}
