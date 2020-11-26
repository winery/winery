import { Component, OnInit } from '@angular/core';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { WineryActions } from '../redux/actions/winery.actions';
import { ToastrService } from 'ngx-toastr';
import { MultiParticipantsService } from '../services/multi-participants.service';
import { TopologyRendererState } from '../redux/reducers/topologyRenderer.reducer';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { backendBaseURL } from '../../../../tosca-management/src/app/configuration';
import { TopologyModelerConfiguration } from '../models/topologyModelerConfiguration';
import { BackendService } from '../services/backend.service';
import { ErrorHandlerService } from '../services/error-handler.service';

@Component({
    selector: 'winery-multi-participants',
    providers: [MultiParticipantsService],
    templateUrl: './multi-participants.component.html',
    styleUrls: ['./multi-participants.component.css']
})
export class MultiParticipantsComponent implements OnInit {

    readonly uiURL = encodeURIComponent(window.location.origin + window.location.pathname + '#/');
    private editorConfiguration;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private wineryActions: WineryActions,
                private alert: ToastrService,
                private errorHandlerService: ErrorHandlerService,
                private multiParticipantsService: MultiParticipantsService,
                private wineryConfigurationService: WineryRepositoryConfigurationService,
                private backendService: BackendService) {
        this.ngRedux.select(state => state.topologyRendererState).subscribe(
            currentButtonState => this.checkButtonsState(currentButtonState)
        );
    }

    private checkButtonsState(currentButtonsState: TopologyRendererState) {
        // check if Generate Placeholder Button is clicked
        if (currentButtonsState.buttonsState.generateGDM) {
            this.multiParticipantsService.postNewVersion().subscribe(
                newVersion => {
                    this.alert.success('Successfully created placeholders for tolopgy template');
                    this.ngRedux.dispatch(this.actions.generatePlaceholder());
                    const editorConfig = '?repositoryURL=' + this.backendService.configuration.repositoryURL
                        + '&uiURL=' + encodeURIComponent(backendBaseURL)
                        + '&ns=' + newVersion.namespace
                        + '&id=' + newVersion.localname;
                    this.editorConfiguration = editorConfig;
                    this.multiParticipantsService.postPlaceholders(newVersion.localname).subscribe(
                        placeholderResponse => {
                            window.open(this.wineryConfigurationService.configuration.endpoints.topologymodeler + this.editorConfiguration);
                        },
                        error => {
                            window.open(this.wineryConfigurationService.configuration.endpoints.topologymodeler + this.editorConfiguration);
                        }
                    );
                },
                error => {
                    this.errorHandlerService.handleError(error);
                }
            );
        } else if (currentButtonsState.buttonsState.generatePlaceholderSubs) {
            this.multiParticipantsService.postSubstituteVersion().subscribe(
                placeholderSubstitution => {
                    this.ngRedux.dispatch(this.actions.generatePlaceholderSubs());
                    const editorConfig = '?repositoryURL=' + this.backendService.configuration.repositoryURL
                        + '&uiURL=' + encodeURIComponent(backendBaseURL)
                        + '&ns=' + placeholderSubstitution.namespace
                        + '&id=' + placeholderSubstitution.localname;
                    this.alert.success('Successfully substituted placeholder for topology');
                    window.open(this.wineryConfigurationService.configuration.endpoints.topologymodeler + editorConfig);
                },
                error => {
                    this.errorHandlerService.handleError(error);
                }
            );
        } else if (currentButtonsState.buttonsState.extractLDM) {
            this.multiParticipantsService.postParticipantsVersion().subscribe(
                participantVersions => {
                    this.ngRedux.dispatch(this.actions.extractLDM());
                    this.alert.success('Successfully extracted partner LDM');
                    for (const participantVersion of participantVersions) {
                        const editorConfiguration = '?repositoryURL=' + this.backendService.configuration.repositoryURL
                            + '&uiURL=' + encodeURIComponent(backendBaseURL)
                            + '&ns=' + participantVersion.entity.namespace
                            + '&id=' + participantVersion.entity.localname;
                        window.open(this.wineryConfigurationService.configuration.endpoints.topologymodeler + editorConfiguration);
                    }
                },
                error => {
                    this.errorHandlerService.handleError(error);
                }
            );
        }
    }

    ngOnInit() {
    }

}
