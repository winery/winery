import {Component} from "@angular/core";
import {Router} from "@angular/router";

@Component({
    selector: 'winery-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.style.css']
})
export class HeaderComponent {

    constructor(private router: Router) {
    }

    showAbout(): void {
        console.log("showing about...");
    }
}
