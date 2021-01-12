import { Component, Input, OnInit } from '@angular/core';
import { ResearchPlugin, TopologyRendererState } from '../../redux/reducers/topologyRenderer.reducer';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../../redux/store/winery.store';
import { TopologyRendererActions } from '../../redux/actions/topologyRenderer.actions';
import { Subscription } from 'rxjs';

@Component({
    selector: 'winery-research-plugins',
    templateUrl: './research-plugins.component.html',
    styleUrls: ['../sidebar.css'],
})
export class ResearchPluginsComponent {

    public researchPluginTypes = ResearchPlugin;

    @Input()
    selectedPlugin: ResearchPlugin;

    refiningType: string;
    subscriptions: Array<Subscription> = [];

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions) {
        this.subscriptions.push(this.ngRedux.select(state => state.topologyRendererState)
            .subscribe(currentButtonsState => this.setButtonsState(currentButtonsState)));
    }

    closeSidebar() {
        this.ngRedux.dispatch(this.actions.disableResearchPlugin());
    }

    private setButtonsState(currentButtonsState: TopologyRendererState) {
        if (currentButtonsState.buttonsState.refineTopologyButton) {
            this.refiningType = 'topology';
        } else if (currentButtonsState.buttonsState.refinePatternsButton) {
            this.refiningType = 'patterns';
        } else if (currentButtonsState.buttonsState.refineTopologyWithTestsButton) {
            this.refiningType = 'tests';
        } else {
            delete this.refiningType;
        }
    }
}
