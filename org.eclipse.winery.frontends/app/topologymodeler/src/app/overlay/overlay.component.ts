import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { Observable, Subscription } from 'rxjs';
import { OverlayService } from '../services/overlay.service';

@Component({
    selector: 'winery-overlay',
    templateUrl: './overlay.component.html',
    styleUrls: ['./overlay.component.css']
})
export class OverlayComponent implements OnInit, OnDestroy {

    _content: string;
    visible: boolean;
    subscriptions: Array<Subscription> = [];

    constructor(
        private overlayService: OverlayService,
        private ngRedux: NgRedux<IWineryState>) {

        // this.subscriptions.push(
        //     this.ngRedux.select(state => state.wineryState.overlayState)
        //         .subscribe(overlayState => {
        //             this.content = overlayState.content.replace(/\.$/, '');
        //             this.visible = overlayState.visible;
        //         }));
    }

    set content(content: string) {
        this._content = content.replace(/\.$/, '');
    }

    ngOnInit() {
        this.overlayService.initOverlayService(this);
    }

    ngOnDestroy(): void {
        // this.subscriptions.forEach(subscription => subscription.unsubscribe());
    }

}
