import { Component, OnInit, Input } from '@angular/core';
import { InheritanceService } from './inheritance.service';
import { InheritanceData } from './inheritanceData';
import { InstanceService } from '../instance.service';


@Component({
    selector: 'winery-instance-inheritance',
    templateUrl: 'inheritance.component.html',
    providers: [InheritanceService],
})
export class InheritanceComponent implements OnInit {

    inheritanceData: InheritanceData;
    openSuperClassLink: string = '';
    loading: boolean = true;

    constructor(
        private sharedData: InstanceService,
        private service: InheritanceService
    ) {}

    ngOnInit() {
        this.service.getInheritanceData(this.sharedData.path)
            .subscribe(inheritance => {
                this.inheritanceData = inheritance;
                this.setButtonLink();
                this.loading = false;
            });
    }

    saveToServer() {
        this.loading = true;
        this.service.saveInheritanceData(this.inheritanceData)
            .subscribe(response => {
               this.loading = false;
               console.log(response);
            });
    }

    onDerivedFromChange(value: string): void {
        this.inheritanceData.derivedFrom = value;
        this.setButtonLink();
    }

    private setButtonLink(): void {
        let parts = this.inheritanceData.derivedFrom.split('}');

        // can be '(none)'
        if (parts.length > 1) {
            let namespace = parts[0].slice(1);
            let name = parts[1];
            this.openSuperClassLink = '/' + this.sharedData.selectedResource.toLowerCase() + 's/' + encodeURIComponent(encodeURIComponent(namespace)) + '/' + name;
        }
    }
}
