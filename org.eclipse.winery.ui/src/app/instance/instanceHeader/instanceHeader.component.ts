import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { ResourceTypes } from '../../resourceTypes.enum';
import { SubmenueCollection } from '../submenue.collection';
import { Component, Input } from '@angular/core';

@Component({
    selector: 'winery-instance-header',
    templateUrl: './instanceHeader.component.html',
    styleUrls: [
        './instanceHeader.component.css'
    ]
})

export class InstanceHeaderComponent {
// TODO: move this "logic" to instance.component and only pass the desired values to here
    submenueCollection: SubmenueCollection = new SubmenueCollection();
    selectedNamespace: string;
    selectedComponent: string;
    @Input() selectedResource: string;
    selectedSubmenueTab: string;

    routeSub: Subscription;
    needTwoLines: boolean = false;
    // woher bekommt man imagePath ?
    imagePath: string = null;
    tabOptions: any[][] = [];


    constructor(private route: ActivatedRoute) {

    }

    ngOnInit(): void {
        this.routeSub = this.route
            .url
            .subscribe(url => {
                for (let item in ResourceTypes) {
                    if (url[0].path === ResourceTypes[item]) {
                        /* to understand:
                         console.log("item : "+ item); //NodeTypes
                         console.log("[item]: "+ ResourceType[item]); //nodetypes
                         console.log("[[item]] : "+ ResourceType[ResourceType[item]]); // NodeTypes
                         */

                        let submenue = this.submenueCollection.getSubmenue(ResourceTypes[ResourceTypes[item]]);
                        let index = 0;
                        let tabCounter = 0;
                        let sTab: string;
                        for (let tab in submenue) {

                            if (index % 2 === 0) {
                                let iTab = tab;
                                if (iTab.length > 3) {
                                    iTab = iTab.replace(/([A-Z])/g, ' $1').trim();
                                }
                                sTab = iTab;
                            } else {
                                this.tabOptions[tabCounter] = [];
                                let href = tab;
                                this.tabOptions[tabCounter].push(sTab);
                                this.tabOptions[tabCounter].push(href);
                                // console.log("index: "+ index + " tabCounter: "+ tabCounter + " sTab: "+ sTab + " href: "+ href);
                                tabCounter++;
                            }
                            index++;
                        }
                        if (this.tabOptions.length > 7) {
                            this.needTwoLines = true;
                        } else {
                            this.needTwoLines = false;
                        }
                        break;
                    }
                }
                this.selectedNamespace = decodeURIComponent(decodeURIComponent(url[1].path));
                this.selectedComponent = url[2].path;

                /* for routing into submenue
                 if( typeof url[3].path === 'undefined'){
                 this.selectedSubmenueTab = "#visualappearance";
                 }else{
                 this.selectedSubmenueTab = url[3].path;
                 }*/
                this.selectedSubmenueTab = '#visualappearance';


            });


    }
}
