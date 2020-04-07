/********************************************************************************
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
import { WineryVersion } from '../../../../tosca-management/src/app/model/wineryVersion';

export class VersionElement {

    versions: WineryVersion[];

    constructor(public qName: string, versions: Array<WineryVersion>) {
        this.versions = [];
        versions.forEach(version => {
            this.versions.push(
                new WineryVersion(version.componentVersion,
                    version.wineryVersion,
                    version.workInProgressVersion,
                    version.currentVersion,
                    version.latestVersion,
                    version.releasable,
                    version.editable)
            );
        });
    }
}
