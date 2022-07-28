import { Injectable } from '@angular/core';
import { BackendService } from './backend.service';
import { ToastrService } from 'ngx-toastr';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { ErrorHandlerService } from './error-handler.service';
import { QNameApiData } from '../../../../tosca-management/src/app/model/qNameApiData';

@Injectable()
export class SplitMatchTopologyService {

    constructor(private alert: ToastrService,
                private backendService: BackendService) {
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
                this.openModelerFor(res.localname,res.namespace, false);
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

    private openModelerFor(id: string, ns: string, readonly: boolean) {
        let editorConfig = '?repositoryURL=' + encodeURIComponent(this.backendService.configuration.repositoryURL)
            + '&uiURL=' + encodeURIComponent(this.backendService.configuration.uiURL)
            + '&ns=' + encodeURIComponent(ns)
            + '&id=' + id
            + '&parentPath=' + this.backendService.configuration.parentPath
            + '&elementPath=' + this.backendService.configuration.elementPath;
        if (readonly) {
            editorConfig += '&isReadonly=true';
        }
        window.open(editorConfig, '_blank');
    }
}
