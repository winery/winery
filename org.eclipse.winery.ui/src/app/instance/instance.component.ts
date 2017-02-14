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
    selectedComponentName: string;
    selectedNamespace: string;
    routeSub: Subscription;

    constructor(private route: ActivatedRoute,
                private service: InstanceService) {
    }

    ngOnInit(): void {
        this.routeSub = this.route
            .data
            .subscribe(data => {
                this.selectedResource = data['resolveData'].section;
                this.selectedNamespace = decodeURIComponent(decodeURIComponent(data['resolveData'].namespace));
                this.selectedComponentName = data['resolveData'].instanceId;

                this.availableTabs = this.service.getSubMenuByResource(this.selectedResource);
            });
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }
}
