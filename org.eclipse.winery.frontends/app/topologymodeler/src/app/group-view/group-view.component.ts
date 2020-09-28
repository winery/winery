import { Component, ElementRef, Input, OnDestroy, OnInit } from '@angular/core';
import { TopologyModelerConfiguration } from '../models/topologyModelerConfiguration';
import { IWineryState } from '../redux/store/winery.store';
import { TopologyRendererActions } from '../redux/actions/topologyRenderer.actions';
import { WineryActions } from '../redux/actions/winery.actions';
import { NgRedux } from '@angular-redux/store';
import { TopologyRendererState } from '../redux/reducers/topologyRenderer.reducer';
import { BackendService } from '../services/backend.service';
import { Group } from '../models/group';
import { TNodeTemplate } from '../models/ttopology-template';
import { SubGroup } from '../models/subgroup';
import { ToastrService } from 'ngx-toastr';
import { TagService } from '../../../../tosca-management/src/app/instance/sharedComponents/tag/tag.service';

@Component({
    selector: 'winery-group-view',
    templateUrl: './group-view.component.html',
    styleUrls: ['./group-view.component.css']
})
export class GroupViewComponent implements OnInit {

    groupClass = 'groupClass';
    subGroupClass = 'subGroupClass';
    showGroupView = false;
    groups: Group[] = [];
    private readonly configuration: TopologyModelerConfiguration;

    constructor(private ngRedux: NgRedux<IWineryState>,
                private actions: TopologyRendererActions,
                private wineryActions: WineryActions,
                private alert: ToastrService,
                private backendService: BackendService,
                private tagService: TagService,
                private host: ElementRef<HTMLElement>) {
        this.configuration = backendService.configuration;
        this.ngRedux.select(state => state.topologyRendererState).subscribe(
            currentButtonState => this.checkButtonsState(currentButtonState)
        );
        this.ngRedux.select(state => state.topologyRendererState).subscribe(
            currentState => this.checkGroupViewState(currentState.updateGroupView)
        );
    }

    private checkButtonsState(currentButtonsState: TopologyRendererState) {
        this.showGroupView = currentButtonsState.buttonsState.groupViewButton;
    }

    private checkGroupViewState(isGroupViewUpdated): void {
        if (isGroupViewUpdated) {
            this.ngRedux.dispatch(this.actions.updateGroupView());
            this.groups = [];
            this.getGroups();
        }
    }

    removeGroup(groupToDelete: Group) {
        for (const subGroup of groupToDelete.subGroup) {
            this.removeSubGroup(groupToDelete, subGroup);
        }
    }

    removeSubGroup(group: Group, subGroup: SubGroup): void {
        for (const member of subGroup.member) {
            this.removeMemberFromSubGroup(group, subGroup, member);
        }
    }

    removeMemberFromSubGroup(group: Group, subGroup: SubGroup, memberToDelete: TNodeTemplate): void {
        this.closeGroupView();
        for (const key in memberToDelete.otherAttributes) {
            if (memberToDelete.otherAttributes.hasOwnProperty(key)) {
                this.removeMember(key, memberToDelete, subGroup);
            }
        }
        for (const key in memberToDelete.otherAttributes) {
            if (memberToDelete.otherAttributes.hasOwnProperty(key)) {
                this.removeSubGroupFromGroup(key, memberToDelete, group);
            }
        }
        this.backendService.requestTopologyTemplateAndVisuals().subscribe(data => {
            let isAnyGroupMemberLeft = false;
            const topologyTemplate = data[0];
            const indexToReplace = topologyTemplate.nodeTemplates.findIndex(nodeTemplate => nodeTemplate.id === memberToDelete.id);
            for (const nodeTemplate of topologyTemplate.nodeTemplates) {
                for (const key in nodeTemplate.otherAttributes) {
                    if (nodeTemplate.otherAttributes.hasOwnProperty(key)) {
                        // check if there is another node template of that group
                        if (nodeTemplate.otherAttributes[key].includes(subGroup.name) && nodeTemplate.id !== memberToDelete.id) {
                            isAnyGroupMemberLeft = true;
                        }
                    }
                }
            }
            console.log(isAnyGroupMemberLeft);
            this.removeParticipantTag(isAnyGroupMemberLeft, subGroup);
            topologyTemplate.nodeTemplates[indexToReplace] = memberToDelete;
            this.backendService.saveTopologyTemplate(topologyTemplate).subscribe(
                tTemplate => {
                    this.alert.success('Successfully removed ' + memberToDelete.id + ' from ' + subGroup.name);
                    this.groups = [];
                    this.getGroups();
                }
            );
        });
    }

    private removeMember(key: string, memberToDelete: TNodeTemplate, subGroup: SubGroup): void {
        let resultMember: string;
        const commaSeparatedListOfMemberValues = memberToDelete.otherAttributes[key].split(',');
        for (const member of commaSeparatedListOfMemberValues) {
            if (member === subGroup.name) {
                delete memberToDelete.otherAttributes[key];
            } else {
                if (!resultMember) {
                    resultMember = member;
                } else {
                    resultMember = resultMember.concat(',', member);
                }
            }
        }
        if (resultMember) {
            memberToDelete.otherAttributes[key] = resultMember;
        }
    }

    private removeSubGroupFromGroup(key: string, memberToDelete, group: Group) {
        let resultSubGroup: string;
        const commaSeparatedListOfMemberValues = memberToDelete.otherAttributes[key].split(',');
        for (const member of commaSeparatedListOfMemberValues) {
            if (member === group.name) {
                delete memberToDelete.otherAttributes[key];
            } else {
                if (!resultSubGroup) {
                    resultSubGroup = member;
                } else {
                    resultSubGroup = resultSubGroup.concat(',', member);
                }
            }
        }
        if (resultSubGroup) {
            memberToDelete.otherAttributes[key] = resultSubGroup;
        }
    }

    private removeParticipantTag(isAnyGroupMemberLeft: boolean, subGroup: SubGroup): void {
        if (isAnyGroupMemberLeft === false) {
            this.tagService.getTagsData(this.backendService.serviceTemplateURL + '/tags/').subscribe(tagsData => {
                for (const tag of tagsData) {
                    if (tag.name === subGroup.name) {
                        this.tagService.removeTagData(tag).subscribe(
                            data => {
                                this.alert.success('Successfully removed tag!');
                            }
                        );
                    }
                }
            });
        }
    }

    private getGroups() {
        this.backendService.requestTopologyTemplateAndVisuals().subscribe(
            data => {
                this.identifyGroupsForNodeTemplates(data[0].nodeTemplates);
            }
        );
    }

    hoverOverSubGroup(subGroup: SubGroup): void {
        const nodeTemplateIds: string[] = [];
        subGroup.member.forEach(entry => nodeTemplateIds.push(entry.id));
        this.ngRedux.dispatch(this.actions.highlightNodes(nodeTemplateIds));
    }

    hoverOutSubGroup(): void {
        this.ngRedux.dispatch(this.actions.highlightNodes([]));
    }

    private identifyGroupsForNodeTemplates(nodeTemplates: TNodeTemplate[]): void {
        for (const nodeTemplate of nodeTemplates) {
            for (const key in nodeTemplate.otherAttributes) {
                if (nodeTemplate.otherAttributes.hasOwnProperty(key)) {
                    const namespace = this.getNameSpaceFromKey(key);
                    if (this.hasGroupsAttribute(nodeTemplate, namespace)) {
                        const groupStringList = this.getCommaSeparatedGroupsList(nodeTemplate, namespace);
                        this.createGroupsListFromStringList(groupStringList);
                    }
                }
            }
        }
        for (const nodeTemplate of nodeTemplates) {
            for (const key in nodeTemplate.otherAttributes) {
                if (nodeTemplate.otherAttributes.hasOwnProperty(key)) {
                    const namespace = this.getNameSpaceFromKey(key);
                    for (const group of this.groups) {
                        if (nodeTemplate.otherAttributes[namespace + group.name]) {
                            const csvSubGroup = nodeTemplate.otherAttributes[namespace + group.name].split(',');

                            for (const csvSubGroupValue of csvSubGroup) {
                                const groupIndex = group.subGroup.findIndex(subGroup => subGroup.name === csvSubGroupValue);
                                if (groupIndex < 0 && nodeTemplate.otherAttributes[namespace + group.name]) {
                                    const subGroup = this.createSubGroupForGroup(csvSubGroupValue, nodeTemplate);
                                    if (group.subGroup.findIndex(subGroupTemp => subGroupTemp.name === subGroup.name) < 0) {
                                        group.subGroup.push(this.createSubGroupForGroup(csvSubGroupValue, nodeTemplate));
                                    }
                                } else if (groupIndex >= 0 && nodeTemplate.otherAttributes[namespace + group.name]) {
                                    // only add to member list if not already there
                                    if (group.subGroup[groupIndex].member.findIndex(nodeT => nodeT === nodeTemplate) < 0) {
                                        group.subGroup[groupIndex].member.push(nodeTemplate);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private getNameSpaceFromKey(key: string): string {
        return key.substring(key.indexOf('{'), key.indexOf('}') + 1);
    }

    private hasGroupsAttribute(nodeTemplate: TNodeTemplate, namespace: string): boolean {
        return nodeTemplate.otherAttributes[namespace + 'groups'];
    }

    private getCommaSeparatedGroupsList(nodeTemplate: TNodeTemplate, namespace: string): string[] {
        return nodeTemplate.otherAttributes[namespace + 'groups'].split(',');
    }

    private createGroupsListFromStringList(groupListString: string[]): void {
        for (const groupName of groupListString) {
            if (!this.groups.find(group => group.name === groupName)) {
                const newGroup: Group = {
                    name: groupName,
                    subGroup: [],
                };
                this.groups.push(newGroup);
            }
        }
    }

    private createSubGroupForGroup(subGroupName: string, nodeTemplate: TNodeTemplate): SubGroup {

        const newSubgroup: SubGroup = {
            name: subGroupName,
            member: [nodeTemplate]
        };

        return newSubgroup;
    }

    ngOnInit() {
        this.getGroups();
    }

    closeGroupView(): void {
        this.ngRedux.dispatch(this.actions.toggleGroupView());
    }

}
