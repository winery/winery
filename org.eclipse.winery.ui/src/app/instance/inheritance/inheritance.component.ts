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

    inheritanceData: InheritanceData;
    loading: boolean = true;

    constructor(
        private sharedData: InstanceService,
        private service: InheritanceService
    ) {}

    ngOnInit() {
        this.service.getInheritanceData(this.sharedData.path)
            .subscribe(inheritance => {
                this.inheritanceData = inheritance;
                this.loading = false;
            });
    }

    saveToServer() {
        console.log('posting');
        /*this.service.saveInheritanceData(this.inheritanceData)
            .subscribe();*/
    }
}
