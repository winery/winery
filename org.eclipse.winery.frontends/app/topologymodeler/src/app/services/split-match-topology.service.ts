import { Injectable } from '@angular/core';
import { BackendService } from './backend.service';
import { ToastrService } from 'ngx-toastr';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { ErrorHandlerService } from './error-handler.service';

@Injectable()
export class SplitMatchTopologyService {

    constructor(private alert: ToastrService) {
    }

    /**
     * Splits the topology.
     * @param backendService    the backend service for accessing the post method for splitting
     * @param ngRedux    the redux instance
     * @param topologyRendererActions    the redux actions for toggling the split button
     */
    splitTopology(backendService: BackendService, ngRedux: NgRedux<IWineryState>, topologyRendererActions: TopologyRendererActions,
                  errorHandler: ErrorHandlerService): void {

        backendService.splitTopology().subscribe(res => {
                ngRedux.dispatch(topologyRendererActions.splitTopology());
                if (res.ok) {
                    const url = res.headers.get('location');
                    this.alert.success('', 'Successfully split.');
                    window.open(url, '_blank');
                }
            },
            error => {
                errorHandler.handleError(error);
                ngRedux.dispatch(topologyRendererActions.splitTopology());
            });
    }

    /**
     * Matches the topology.
     * @param backendService    the backend service for accessing the post method for matching
     * @param ngRedux    the redux instance
     * @param topologyRendererActions    the redux actions for toggling the match button
     */
    matchTopology(backendService: BackendService, ngRedux: NgRedux<IWineryState>, topologyRendererActions: TopologyRendererActions,
                  errorHandler: ErrorHandlerService): void {
        backendService.matchTopology().subscribe(res => {
                ngRedux.dispatch(topologyRendererActions.matchTopology());
                if (res.ok) {
                    const url = res.headers.get('location');
                    this.alert.success('', 'Successfully matched.');
                    window.open(url, '_blank');
                }
            },
            error => {
                errorHandler.handleError(error);
                ngRedux.dispatch(topologyRendererActions.matchTopology());
            });
    }
}
