import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RemoveWhiteSpacesPipe } from '../../pipes/removeWhiteSpaces.pipe';

@Component({
    selector: 'winery-instance-header',
    templateUrl: './instanceHeader.component.html',
    styleUrls: [
        './instanceHeader.component.css'
    ],
    providers: [
        RemoveWhiteSpacesPipe
    ]
})

export class InstanceHeaderComponent implements OnInit {

    @Input() selectedNamespace: string;
    @Input() selectedComponentName: string;
    @Input() selectedResource: string;
    @Input() subMenu: string[];

    needTwoLines: boolean = false;
    selectedTab: string;

    constructor(private router: Router) {}

    ngOnInit(): void {
        console.log(this.subMenu);
        if (this.subMenu.length > 7) {
            this.needTwoLines = true;
        }
    }
}
