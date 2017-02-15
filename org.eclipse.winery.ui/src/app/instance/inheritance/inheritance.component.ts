import { Component, OnInit, Input } from '@angular/core';
import { InheritanceService } from "./inheritance.service";
import { InheritanceData } from "./inheritanceData";

@Component({
    selector: 'winery-instance-inheritance',
    templateUrl: 'inheritance.component.html',
    providers: [InheritanceService]

})

export class InheritanceComponent implements OnInit {

    // @Input() abstract: boolean;
    // @Input() final: boolean;
    // @Input() derivedFrom: any[];

    inheritanceData: InheritanceData;


    constructor(private service: InheritanceService) {
    }

    ngOnInit() {
        this.inheritanceData = this.service.getInheritanceData()
    }

    saveToServer() {
        console.log("saveToServer called")
    }
}
