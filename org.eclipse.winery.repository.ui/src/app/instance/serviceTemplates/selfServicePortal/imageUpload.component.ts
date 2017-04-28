/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Component, Input, OnInit } from '@angular/core';

@Component({
    selector: 'winery-image-upload',
    templateUrl: 'imageUpload.component.html'
})
export class ImageUploadComponent implements OnInit {
    @Input() label = '';
    @Input() url = '';
    @Input() imageWidth = '16px';
    @Input() id: string;
    @Input() accept = 'image/*';

    constructor() {
    }

    ngOnInit() {
    }

}
