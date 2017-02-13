import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { sections } from '../sections.config';
import { InstanceService } from './instance.service';
import { InstanceData } from './instanceData';


@Component({
    templateUrl: 'instance.component.html',
    providers: [
        InstanceService
    ]
})
export class InstanceComponent implements OnInit, OnDestroy {
    componentData: InstanceData;
    selectedResource: string;
    routeSub: Subscription;

    constructor(private route: ActivatedRoute,
        private service: InstanceService
    ) { }

    ngOnInit(): void {
        this.routeSub = this.route
            .url
            .subscribe(url => {
                console.log(url);
                this.selectedResource = sections[url[0].path];
                this.componentData = this.service.getInstanceData(this.selectedResource);
            });
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }
}
