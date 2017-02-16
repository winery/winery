import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { InstanceService } from './instance.service';

@Component({
    templateUrl: 'instance.component.html',
    providers: [
        InstanceService
    ]
})
export class InstanceComponent implements OnInit, OnDestroy {

    availableTabs: string[];
    selectedResource: string;
    selectedComponentId: string;
    selectedNamespace: string;
    path: string;

    routeSub: Subscription;

    constructor(private route: ActivatedRoute,
                private service: InstanceService) {
    }

    ngOnInit(): void {
        this.routeSub = this.route
            .data
            .subscribe(data => {
                this.selectedResource = data['resolveData'].section;
                this.selectedNamespace = data['resolveData'].namespace;
                this.selectedComponentId = data['resolveData'].instanceId;
                this.path = data['resolveData'].path;

                this.service.setSharedData(this.selectedResource, this.selectedNamespace, this.selectedComponentId, this.path);

                this.availableTabs = this.service.getSubMenuByResource();
            });
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }
}
