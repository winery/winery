import { Component, OnInit, OnDestroy } from '@angular/core';
import { NavigationEnd, Router, ActivatedRoute, Params } from '@angular/router';

import { Subscription } from 'rxjs';

import { SectionService } from './section.service';
import { SectionData } from './sectionData';
import { sections } from '../sections.config';
import { isNullOrUndefined } from 'util';

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
