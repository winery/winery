import { Component, OnInit, ViewChild } from '@angular/core';
import { IWineryState } from '../redux/store/winery.store';
import { NgRedux } from '@angular-redux/store';
import { TGroupDefinition } from '../models/ttopology-template';
import { WineryActions } from '../redux/actions/winery.actions';
import { DynamicTextData } from '../../../../tosca-management/src/app/wineryDynamicTable/formComponents/dynamicText.component';
import { WineryDynamicTableMetadata } from '../../../../tosca-management/src/app/wineryDynamicTable/wineryDynamicTableMetadata';
import { WineryDynamicFormModalComponent } from '../../../../tosca-management/src/app/wineryDynamicTable/modal/wineryDynamicFormModal.component';

@Component({
    selector: 'winery-group-view',
    templateUrl: './group-view.component.html',
    styleUrls: ['./group-view.component.css']
})
export class GroupViewComponent implements OnInit {

    showGroupView = false;
    groups: TGroupDefinition[] = [];
    formMetadata: Array<WineryDynamicTableMetadata> = [];

    private expandedDefinitions: TGroupDefinition[] = [];

    @ViewChild('generatedModal') generatedModal: WineryDynamicFormModalComponent;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private ngActions: WineryActions) {
        this.ngRedux.select((state) => state.topologyRendererState.buttonsState.manageYamlGroupsButton)
            .subscribe((showGroupView) => this.showGroupView = showGroupView);
        this.ngRedux.select((state) => state.wineryState.currentJsonTopology)
            .subscribe((topology) => this.groups = topology.groups);
    }

    ngOnInit() {
        this.formMetadata = [
            new DynamicTextData('name', 'Name',
                0, [], '', false, false, true),
            new DynamicTextData('description', 'Description',
                1, [], '', false, false, true),
        ];
    }

    addGroup() {
        this.generatedModal.show({});
    }

    onSaveClicked(param: any) {
        const newGroups = Object.assign([], this.groups);
        newGroups.push(new TGroupDefinition(param.name, param.description, []));
        this.ngRedux.dispatch(this.ngActions.updateGroupDefinitions(newGroups));
    }

    removeGroup(definition: TGroupDefinition) {
        const newGroups = Object.assign([], this.groups);
        const index = newGroups.findIndex((d) => d.name === definition.name);
        newGroups.splice(index, 1);
        this.ngRedux.dispatch(this.ngActions.updateGroupDefinitions(newGroups));
    }

    isEmpty(): boolean {
        return !this.groups || this.groups.length === 0;
    }

    isCollapsed(definition: TGroupDefinition) {
        return !this.expandedDefinitions.some((d) => d.name === definition.name);
    }

    toggleCollapse(definition: TGroupDefinition) {
        const index = this.expandedDefinitions.findIndex((d) => d.name === definition.name);
        if (index >= 0) {
            this.expandedDefinitions.splice(index, 1);
        } else {
            this.expandedDefinitions.push(definition);
        }
    }
}
