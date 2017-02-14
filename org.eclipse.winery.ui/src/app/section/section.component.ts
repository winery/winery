import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SectionService } from './section.service';
import { SectionData } from './sectionData';
import { sections } from '../configuration';

@Component({
    selector: 'winery-section-component',
    templateUrl: 'section.component.html',
    providers: [
        SectionService,
    ]
})
export class SectionComponent implements OnInit, OnDestroy {

    componentData: SectionData[];
    selectedResource: string;
    routeSub: Subscription;

    constructor(private route: ActivatedRoute,
                private service: SectionService,
    ) { }

    /**
     * @override
     *
     * Subscribe to the url on initialisation in order to get the corresponding resource type.
     */
    ngOnInit(): void {
        this.routeSub = this.route
            .data
            .subscribe(data => {
                this.selectedResource = data['resolveData'].section;
                this.componentData = this.service.getSectionData(this.selectedResource);
            });
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }
}
