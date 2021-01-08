import { Component, OnInit, ViewChild } from '@angular/core';
import { IWineryState } from '../redux/store/winery.store';
import { NgRedux } from '@angular-redux/store';
import { OTParticipant } from '../models/ttopology-template';
import { WineryActions } from '../redux/actions/winery.actions';
import { DynamicTextData } from '../../../../tosca-management/src/app/wineryDynamicTable/formComponents/dynamicText.component';
import { WineryDynamicTableMetadata } from '../../../../tosca-management/src/app/wineryDynamicTable/wineryDynamicTableMetadata';
import { WineryDynamicFormModalComponent } from '../../../../tosca-management/src/app/wineryDynamicTable/modal/wineryDynamicFormModal.component';

@Component({
    selector: 'winery-manage-participants',
    templateUrl: './manage-participants.component.html',
    styleUrls: ['./manage-participants.component.css']
})
export class ManageParticipantsComponent implements OnInit {

    visible = false;
    participants: OTParticipant[] = [];
    formMetadata: Array<WineryDynamicTableMetadata> = [];

    @ViewChild('generatedModal') generatedModal: WineryDynamicFormModalComponent;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private ngActions: WineryActions) {
        this.ngRedux.select((state) => state.topologyRendererState.buttonsState.manageParticipantsButton)
            .subscribe((visible) => this.visible = visible);
        this.ngRedux.select((state) => state.wineryState.currentJsonTopology)
            .subscribe((topology) => this.participants = topology.participants);
    }

    ngOnInit() {
        this.formMetadata = [
            new DynamicTextData('name', 'Name',
                0, [], '', false, false, true),
            new DynamicTextData('url', 'URL',
                1, [], '', false, false, true),
        ];
    }

    openModal() {
        this.generatedModal.show({});
    }

    save(param: any) {
        const newParticipants = Object.assign([], this.participants);
        newParticipants.push({ name: param.name, url: param.url } as OTParticipant);
        this.ngRedux.dispatch(this.ngActions.updateParticipants(newParticipants));
    }

    remove(participant: OTParticipant) {
        const newParticipants = Object.assign([], this.participants);
        const index = newParticipants.findIndex((p) => p.name === participant.name);
        newParticipants.splice(index, 1);
        this.ngRedux.dispatch(this.ngActions.updateParticipants(newParticipants));
    }

    isEmpty(): boolean {
        return !this.participants || this.participants.length === 0;
    }
}
