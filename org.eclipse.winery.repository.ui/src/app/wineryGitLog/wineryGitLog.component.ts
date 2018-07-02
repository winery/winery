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
import { GitChange, GitData, GitResponseData } from './GitLogApiData';
import { isNullOrUndefined } from 'util';

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
    files: GitChange[] = [];
    selectedFile: GitChange;
    commitMsg = '';
    show = false;

    @ViewChild('confirmDiscardModal') confirmDiscardModal: ModalDirective;

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

            if (!isNullOrUndefined(data.changes)) {
                this.files = data.changes;
            }

            if (!isNullOrUndefined(data.success)) {
                this.notify.success(data.success);
            }

            if (!isNullOrUndefined(data.error)) {
                this.notify.error(data.error);
            }

            if (!isNullOrUndefined(data.resetSuccess)) {
                this.router.navigate(['/']);
            }

            if (!isNullOrUndefined(data.lfsAvailable)) {
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
        if (this.files === null || this.files.length === 0) {
            this.notify.error('A commit must contain at least one change!');
        } else if (this.commitMsg === '') {
            this.notify.error('Please enter a valid commit message!');
        } else {
            this.webSocket.send(JSON.stringify({ commitMessage: this.commitMsg }));
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

    select(file: GitChange) {
        if (this.selectedFile === file) {
            this.selectedFile = null;
        } else {
            this.selectedFile = file;
        }
    }

    onExpand() {
        this.isExpanded = !this.isExpanded;
        this.selectedFile = null;
    }

    discardChanges() {
        const data = new GitData();
        data.reset = true;
        this.webSocket.send(JSON.stringify(data));
        this.hide();
    }

    hide() {
        this.selectedFile = null;
        this.isExpanded = false;
    }
}
