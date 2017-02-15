import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SectionService } from './section.service';
import { SectionData } from './sectionData';

@Component({
    selector: 'winery-section-component',
    templateUrl: 'section.component.html',
    providers: [
        SectionService,
    ]
})
export class SectionComponent implements OnInit, OnDestroy {

    componentData: SectionData[];
    loading: boolean = true;
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
            .subscribe(data => this.getComponentData(data));
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }

    private getComponentData(data: any) {
        let resolved = data['resolveData'];
        this.selectedResource = resolved.section;
        this.service.getSectionData(resolved.path)
            .subscribe(resources => {
               this.componentData = resources;
               this.loading = false;
            });
    }
}
