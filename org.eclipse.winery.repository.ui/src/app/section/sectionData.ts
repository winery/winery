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
import { Utils } from '../wineryUtils/utils';
import { WineryVersion } from '../wineryInterfaces/wineryVersion';

/**
 * Type definition for data returned by the section service.
 */
export class SectionData {
    id?: string;
    name?: string;
    namespace: string;
    count?: number;
    version?: WineryVersion;
    versionInstances?: SectionData[];
    hasChildren?: boolean;

    createContainerCopy(original: SectionData): SectionData {
        this.id = Utils.getNameWithoutVersion(original.id);
        this.name = Utils.getNameWithoutVersion(original.name);
        this.namespace = original.namespace;
        this.version = original.version;
        this.versionInstances = [original];

        return this;
    }

    createCopy(original: SectionData): SectionData {
        this.id = original.id;
        this.name = original.name;
        this.namespace = original.namespace;
        this.version = original.version;

        return this;
    }
}
