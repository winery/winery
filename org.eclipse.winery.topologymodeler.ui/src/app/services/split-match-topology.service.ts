import { Injectable } from '@angular/core';
import { BackendService } from './backend.service';
import { WineryAlertService } from '../winery-alert/winery-alert.service';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable()
export class SplitMatchTopologyService {
    private url: string;

    constructor(private alert: WineryAlertService) {
    }

    /**
     * Splits the topology.
     * @param backendService    the backend service for accessing the post method for splitting
     * @param ngRedux    the redux instance
     * @param topologyRendererActions    the redux actions for toggling the split button
     */
    splitTopology(backendService: BackendService, ngRedux: NgRedux<IWineryState>, topologyRendererActions: TopologyRendererActions): void {

        backendService.splitTopology().subscribe(res => {
                ngRedux.dispatch(topologyRendererActions.splitTopology());
                if (res.ok) {
                    const url = res.headers.get('location');
                    this.alert.success('', 'Successfully split.');
                    window.open(url, '_blank');
                }
            },
            error => {
                this.handleError(error);
                ngRedux.dispatch(topologyRendererActions.splitTopology());
            });
    }

    /**
     * Matches the topology.
     * @param backendService    the backend service for accessing the post method for matching
     * @param ngRedux    the redux instance
     * @param topologyRendererActions    the redux actions for toggling the match button
     */
    matchTopology(backendService: BackendService, ngRedux: NgRedux<IWineryState>, topologyRendererActions: TopologyRendererActions): void {
        backendService.matchTopology().subscribe(res => {
                ngRedux.dispatch(topologyRendererActions.matchTopology());
                if (res.ok) {
                    const url = res.headers.get('location');
                    this.alert.success('', 'Successfully matched.');
                    window.open(url, '_blank');
                }
            },
            error => {
                this.handleError(error);
                ngRedux.dispatch(topologyRendererActions.matchTopology());
            });
    }

    /**
     * Error handler.
     * @param error    the error
     */
    private handleError(error: HttpErrorResponse) {
        if (error.error instanceof ErrorEvent) {
            this.alert.info('<p>Something went wrong! <br>' + 'Response Status: '
                + error.statusText + ' ' + error.status + '</p><br>' + error.error.message);
        } else {
            this.alert.info('<p>Something went wrong! <br>' + 'Response Status: '
                + error.statusText + ' ' + error.status + '</p><br>' + error.error);
        }
    }

}
