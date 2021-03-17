/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { ModalDirective } from 'ngx-bootstrap';
import { Router } from '@angular/router';
import { webSocketURL } from '../configuration';
import { GitChange, GitData, Repos, GitResponseData } from './GitLogApiData';

@Component({
    selector: 'winery-gitlog',
    templateUrl: 'wineryGitLog.component.html',
    styleUrls: [
        'wineryGitLog.component.css'
    ]
})
export class WineryGitLogComponent implements OnInit {

    webSocket: WebSocket;
    isExpanded = false;
    lfsAvailable = false;
    repos: Repos[] = [];
    branches: String[] = [];
    selectedBranch: string;
    selectedRepo: string;
    selectedFile: GitChange;
    commitMsg = '';
    show = false;
    command = '';
    filesToCommit: string[] = [];

    @ViewChild('confirmDiscardModal') confirmDiscardModal: ModalDirective;
    @ViewChild('confirmMassExecution') confirmMassExecution: ModalDirective;
    @ViewChild('selectBranch') selectBranch: ModalDirective;

    constructor(private notify: WineryNotificationService,
                private router: Router) {
    }

    ngOnInit() {
        this.webSocket = new WebSocket(webSocketURL + '/git');
        this.webSocket.onopen = event => {
            this.refreshLog();
            this.show = true;
        };

        this.webSocket.onmessage = event => {
            const data: GitResponseData = JSON.parse(event.data);

            if (data.repos) {
                this.repos = data.repos;
                this.filesToCommit.length = 0;
            }

            if (data.branches) {
                this.branches = data.branches;
            }

            if (data.success) {
                this.notify.success(data.success);
            }

            if (data.error) {
                this.notify.error(data.error);
            }

            if (data.resetSuccess) {
                this.router.navigate(['/']);
            }

            if (data.lfsAvailable) {
                this.lfsAvailable = data.lfsAvailable;
            }

            this.commitMsg = '';
            this.selectedFile = null;
        };

        this.webSocket.onclose = event => {
            this.webSocket.close();
        };
    }

    commit() {
        if (this.selectedRepo == null) {
            this.notify.error('Select the Repository you want to create a commit to!');
        } else if (this.commitMsg === '') {
            this.notify.error('Please enter a valid commit message!');
        } else if (this.repos === null || this.repos.length === 0) {
            this.notify.error('A commit must contain at least one change!');
        } else {

            for (const repo of this.repos) {
                if (repo.name === this.selectedRepo) {
                    if (repo.changes.length === 0) {
                        this.notify.error('A commit must contain at least one change!');
                    } else {
                        const data = new GitData();
                        data.commit = true;
                        data.commitMessage = this.commitMsg;
                        data.repository = this.selectedRepo;
                        data.itemsToCommit = this.filesToCommit;

                        this.webSocket.send(JSON.stringify(data));
                    }
                }
            }
        }
    }

    refreshLog() {
        const data = new GitData();
        data.refresh = true;
        this.webSocket.send(JSON.stringify(data));
    }

    doCommitMsgValueChange(data: any) {
        this.commitMsg = data.target.value;
    }

    select(file: GitChange, repoName: string) {

        if (this.selectedFile === file) {


            this.selectedFile = null;
            if (this.selectedRepo === repoName) {
                this.selectedRepo = null;
            }
        } else {
            this.selectedFile = file;
            this.selectedRepo = repoName;
        }

    }

    onExpand() {
        this.isExpanded = !this.isExpanded;
        this.selectedFile = null;
    }

    discardChanges() {
        const data = new GitData();
        data.reset = true;
        data.repository = this.selectedRepo;
        this.webSocket.send(JSON.stringify(data));
    }

    hide() {
        this.selectedFile = null;
        this.isExpanded = false;
    }

    multipleRepositoryCheck(command: string) {
        if (this.selectedRepo == null) {
            this.command = command;
            this.confirmMassExecution.show();
        } else {
            if (command === 'push') {
                this.push();
            } else if (command === 'pull') {
                this.pull();
            }
        }
    }

    selectRepo(repoName: string) {
        if (this.selectedRepo === repoName && this.selectedFile == null) {
            this.selectedRepo = null;
        } else {
            this.selectedRepo = repoName;
        }
    }

    pull() {
        const data = new GitData();
        data.pull = true;
        data.repository = this.selectedRepo;
        this.webSocket.send(JSON.stringify(data));
    }

    push() {
        const data = new GitData();
        data.push = true;
        data.repository = this.selectedRepo;
        this.webSocket.send(JSON.stringify(data));
    }

    selectFileToCommit(file: GitChange, repoName: string, event: any) {

        if (event.target.checked) {

            if (this.selectedRepo !== repoName) {
                this.filesToCommit = [];
            }

            this.selectedRepo = repoName;
            this.filesToCommit.push(file.name);

        } else {

            this.filesToCommit = this.filesToCommit.filter((value) => value !== file.name);

        }
    }

    checkout() {
        const data = new GitData();
        data.checkout = this.selectedBranch;
        data.repository = this.selectedRepo;
        this.webSocket.send(JSON.stringify(data));
    }

    getBranches() {
        const data = new GitData();
        data.branches = true;
        data.repository = this.selectedRepo;
        this.webSocket.send(JSON.stringify(data));
        this.selectBranch.show();
    }
}
