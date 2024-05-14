/*******************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InstanceService } from '../../instance.service';
import { Observable } from 'rxjs';

export interface DetectionModel {
    isPdrm: boolean;
}

@Injectable()
export class DetectionService {
    private readonly path: string;

    constructor(private http: HttpClient, private sharedData: InstanceService) {
        this.path = this.sharedData.path + '/detectionmodel';
    }

    public setDetectionModel(model: DetectionModel): Observable<DetectionModel> {
        return this.http.post<DetectionModel>(this.path, model);
    }

    public getDetectionModel(): Observable<DetectionModel> {
        return this.http.get<DetectionModel>(this.path);
    }
}
