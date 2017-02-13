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
                private router: Router) {

    }

    /**
     * @override
     *
     * Subscribe to the url on initialisation in order to get the corresponding resource type.
     */
    ngOnInit(): void {
        this.routeSub = this.route
            .url
            .subscribe(url => {
                this.selectedResource = sections[url[0].path];
                this.componentData = this.service.getSectionData(this.selectedResource);
            });
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }
}
