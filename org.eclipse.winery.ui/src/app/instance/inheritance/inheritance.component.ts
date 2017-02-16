import { Component, OnInit, Input } from '@angular/core';
import { InheritanceService } from "./inheritance.service";
import { InheritanceData } from "./inheritanceData";
import { InstanceService } from '../instance.service';

@Component({
    selector: 'winery-instance-inheritance',
    templateUrl: 'inheritance.component.html',
    providers: [InheritanceService],
})
export class InheritanceComponent implements OnInit {

    selectedResource: string;
    selectedComponentId: string;
    selectedNamespace: string;
    path: string;

    inheritanceData: InheritanceData;
    loading: boolean = true;

    constructor(
        private sharedData: InstanceService,
        private service: InheritanceService
    ) {}

    ngOnInit() {
        this.selectedComponentId = this.sharedData.selectedComponentId;
        this.selectedNamespace = this.sharedData.selectedNamespace;
        this.selectedResource = this.sharedData.selectedResource;
        this.path = this.sharedData.path;

        this.service.getInheritanceData(this.path)
            .subscribe(inheritance => {
                this.inheritanceData = inheritance;
                this.loading = false;
            });

        this.inheritanceData = {
            abstract: true,
            final: false,
            derivedFrom: [
                {name: 'test1', QName: '{http://example.org}test1', selected: false},
                {name: 'test2', QName: '{http://example.org}test2', selected: true},
                {name: 'test3', QName: '{http://example.org}test3', selected: false},
                {name: 'test4', QName: '{http://example.org}test4', selected: false},
                {name: 'test5', QName: '{http://example.org}test5', selected: false}
            ]
        }
    }

    saveToServer() {
        console.log('posting');
        /*this.service.saveInheritanceData(this.inheritanceData)
            .subscribe();*/
    }
}
