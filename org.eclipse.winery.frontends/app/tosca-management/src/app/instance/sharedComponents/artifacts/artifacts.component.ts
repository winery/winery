/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
import { Component, OnInit, ViewChild } from '@angular/core';
import { ArtifactsService } from './artifacts.service';
import { NameAndQNameApiDataList } from '../../../wineryQNameSelector/wineryNameAndQNameApiData';
import { Artifact } from '../../../model/artifact';
import { ModalDirective } from 'ngx-bootstrap';
import { InstanceService } from '../../instance.service';
import { WineryValidatorObject } from '../../../wineryValidators/wineryDuplicateValidator.directive';

@Component({
    selector: 'winery-instance-artifacts',
    templateUrl: 'artifacts.component.html',
})
export class ArtifactsComponent implements OnInit {

    loading: boolean;

    artifacts: Artifact[] = [];
    selectedArtifact: Artifact = new Artifact();
    artifactTypes: NameAndQNameApiDataList = { classes: null };
    columns = [
        { title: 'Name', name: 'name' },
        { title: 'Type', name: 'type' },
        { title: 'File', name: 'file' },
        { title: 'Description', name: 'description' },
        { title: 'Deployment Path', name: 'deployPath' }
    ];

    @ViewChild('modal') modal: ModalDirective;
    @ViewChild('removeModal') removeModal: ModalDirective;
    validatorObject: WineryValidatorObject;

    selectedFile: File;
    allowedTypes = '';

    constructor(private artifactsService: ArtifactsService, public instanceService: InstanceService) {
    }

    ngOnInit(): void {
        this.loading = true;
        this.artifactsService.getArtifacts().subscribe(data => {
            this.artifacts = [];
            data.forEach(item => this.artifacts.push({ ...new Artifact(), ...item }));
            this.loading = false;
        }, error => ArtifactsComponent.handleError(error));
        this.artifactsService.getArtifactTypes().subscribe(data => {
            this.artifactTypes.classes = data;
        }, error => ArtifactsComponent.handleError(error));
    }

    private static handleError(error: any) {
        console.error(error);
    }

    openModal() {
        this.selectedArtifact = new Artifact();
        this.validatorObject = new WineryValidatorObject(this.artifacts, 'name');
        this.modal.show();
    }

    addArtifact() {
        this.artifacts.push(Object.assign(new Artifact(), this.selectedArtifact));
        this.loading = true;
        this.artifactsService.createArtifact(this.selectedArtifact, this.selectedFile).subscribe(() => {
            this.loading = false;
            this.selectedArtifact = new Artifact();
        });
    }

    openRemoveModal(artifact: Artifact) {
        if (artifact === null || artifact === undefined) {
            return;
        }
        this.selectedArtifact = artifact;
        this.removeModal.show();
    }

    removeArtifact() {
        const arr = this.artifacts;
        for (let i = 0; i < arr.length; i++) {
            if (arr[i].name === this.selectedArtifact.name) {
                arr.splice(i, 1);
            }
        }
        this.loading = true;
        this.artifactsService.deleteArtifact(this.selectedArtifact).subscribe(() => {
            this.selectedArtifact = new Artifact();
            this.ngOnInit();
        });
    }

    onArtifactTypeChanged(value: string) {
        this.allowedTypes = ''; // TODO
        if (this.selectedArtifact) {
            this.selectedArtifact.type = value;
        }
    }

    fileSelected(files: any) {
        if (files.length > 0) {
            this.selectedFile = files[0];
            this.selectedArtifact.file = this.selectedFile.name;
        } else {
            this.selectedFile = undefined;
        }
    }
}
