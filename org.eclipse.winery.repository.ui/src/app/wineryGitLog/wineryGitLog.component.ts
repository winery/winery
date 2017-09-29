/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import { GitLogApiData } from './GitLogApiData';
import { WineryNotificationService } from '../wineryNotificationModule/wineryNotification.service';
import { ModalDirective } from 'ngx-bootstrap';
import { Router } from '@angular/router';

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
    files: GitLogApiData[] = [];
    selectedFile: GitLogApiData;
    commitMsg = '';
    show = false;

    @ViewChild('confirmDiscardModal') confirmDiscardModal: ModalDirective;

    constructor(private notify: WineryNotificationService,
                private router: Router) {
    }

    ngOnInit() {
        this.webSocket = new WebSocket('ws://' + location.hostname + ':8080/winery/git');
        this.webSocket.onopen = event => {
            this.refreshLog();
            this.show = true;
        };

        this.webSocket.onmessage = event => {
            if (event.data === 'commit success') {
                this.notify.success('Commited: ' + this.commitMsg);
                this.commitMsg = '';
                this.selectedFile = null;
            } else if (event.data === 'commit failed') {
                this.notify.error('commit failed');
            } else if (event.data === 'reset failed') {
                this.notify.error('winery-repository reset to last commit failed!');
            } else if (event.data === 'reset success') {
                this.notify.success('winery-repository resetted to last commit');
                this.router.navigate(['/']);
            } else {
                this.files = JSON.parse(event.data);
                for (let i = 0; i < this.files.length; i++) {
                    this.files[i].name = decodeURIComponent(decodeURIComponent(this.files[i].name));
                }
            }
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
            this.webSocket.send(this.commitMsg);
        }
    }

    refreshLog() {
        this.webSocket.send('');
    }

    doCommitMsgValueChange(data: any) {
        this.commitMsg = data.target.value;
    }

    select(file: GitLogApiData) {
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
        this.webSocket.send('reset');
        this.selectedFile = null;
    }

    hide() {
        this.isExpanded = false;
    }
}
