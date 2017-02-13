import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { sections, Configuration } from '../configuration';
import { InstanceService } from './instance.service';
import { InstanceData } from './instanceData';

@Component({
    templateUrl: 'instance.component.html',
    providers: [
        InstanceService
    ]
})
export class InstanceComponent implements OnInit, OnDestroy {

    availableTabs: string[];
    componentData: InstanceData;
    selectedResource: string;
    selectedComponentName: string;
    selectedNamespace: string;
    routeSub: Subscription;

    constructor(private route: ActivatedRoute,
                private service: InstanceService) {
    }

    ngOnInit(): void {
        this.routeSub = this.route
            .url
            .subscribe(url => {
                this.selectedResource = sections[url[0].path];
                this.selectedNamespace = decodeURIComponent(decodeURIComponent(url[1].path));
                this.selectedComponentName = url[2].path;

                this.componentData = this.service.getInstanceData(this.selectedResource);

                this.getSubMenuForResource();
            });
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }

    getSubMenuForResource() {
        this.availableTabs = Configuration.getSubMenuByResource(this.selectedResource);
    }
}
