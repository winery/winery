/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import { Component, OnInit } from '@angular/core';
import { WineryConfiguration, WineryRepositoryConfigurationService } from '../../../wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { HttpClient } from '@angular/common/http';
import { backendBaseURL } from '../../../configuration';
import { WineryNotificationService } from '../../../wineryNotificationModule/wineryNotification.service';
import { FormControl } from '@angular/forms';

@Component({
    selector: 'winery-instance-configuration-component',
    templateUrl: 'configuration.component.html',
    styleUrls: [
        'configuration.component.css',
    ]
})

export class FeatureConfigurationComponent implements OnInit {
    config: WineryConfiguration;
    public containerUrlAvailable: boolean;
    public containerUrlControl: FormControl = new FormControl();
    public containerUrl;
    container : any;
    constructor(private http: HttpClient,
                private configData: WineryRepositoryConfigurationService,
                private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        this.config = this.configData.configuration;
        let pos1 = backendBaseURL.indexOf(":");      
        let pos2 = backendBaseURL.indexOf(":", pos1 + 1); 
        let url = backendBaseURL.substring(0, pos2 + 1) + '4242';
        this.containerUrlControl.setValue(url);
        this.containerUrl = url;
        this.checkModelerAvailability();
    }

    checkModelerAvailability(){
        const containerURL = (<HTMLInputElement> document.getElementById('containerUrl2')).value;
        console.log(containerURL);
        if(containerURL!=''){
            this.containerUrl = containerURL;
                this.http.get(this.containerUrl, { responseType: 'text' }).subscribe(
                    () => {this.containerUrlAvailable = true; console.log("success")},
                    error => {this.containerUrlAvailable = false;}
                )}
        
        if(this.containerUrl !=''){
                this.http.get(this.containerUrl, { responseType: 'text' }).subscribe(
                    () => {this.containerUrlAvailable = true; console.log("success")},
                    error => {this.containerUrlAvailable = false;}
                )}
    }
    
    saveModelerURL(){
        if(this.containerUrlAvailable) {
            const containerURL = (<HTMLInputElement>document.getElementById('containerUrl2')).value;
            this.config.endpoints.bpmnModeler = containerURL;
            this.http.put<WineryConfiguration>(backendBaseURL + '/admin' + '/config', this.config)
                .subscribe(
                    () => this.notify.success('Successfully saved Modeler URL!'),
                    () => this.notify.error('Error while saving Modeler URL')
                );
        }else{
            this.notify.error('Modeler URL is not available')
        }
    }
    
    deleteModelerURL(){
        this.containerUrlControl.setValue("");
    }
    
    saveChanges() {
        console.log(this.config.endpoints.bpmnModeler="Http:localhost:4242");
        console.log(backendBaseURL + '/admin' + '/config');
        this.http.put<WineryConfiguration>(backendBaseURL + '/admin' + '/config', this.config)
            .subscribe(
                () => this.notify.success('Successfully saved changes!'),
                () => this.notify.error('Error while saving changes')
            );
    }
}
