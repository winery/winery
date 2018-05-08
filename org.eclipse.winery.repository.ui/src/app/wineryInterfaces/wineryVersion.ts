/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import {isNullOrUndefined} from 'util';

export class WineryVersion {

    public static readonly WINERY_NAME_FROM_VERSION_SEPARATOR = '_';
    public static readonly WINERY_VERSION_SEPARATOR = '-';
    public static readonly WINERY_VERSION_PREFIX = 'w';
    public static readonly WINERY_WORK_IN_PROGRESS_PREFIX = 'wip';
    public static readonly EMPTY_STRING = '[no version identifier]';

    constructor(public componentVersion: string,
                public wineryVersion: number,
                public workInProgressVersion: number,
                public readonly currentVersion = false,
                public readonly latestVersion = false,
                public readonly releasable = false,
                public readonly editable = true) {
    }

    toString(): string {
        let versionString = this.componentVersion ? this.componentVersion : '';

        if (!isNullOrUndefined(this.wineryVersion) && this.wineryVersion > 0) {
            versionString += WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_VERSION_PREFIX + this.wineryVersion;

            if (!isNullOrUndefined(this.workInProgressVersion) && this.workInProgressVersion > 0) {
                versionString += WineryVersion.WINERY_VERSION_SEPARATOR + WineryVersion.WINERY_WORK_IN_PROGRESS_PREFIX + this.workInProgressVersion;
            }
        }

        return versionString;
    }

    toReadableString(): string {
        let versionString = this.toString();

        if (versionString.length === 0) {
            versionString = WineryVersion.EMPTY_STRING;
        }

        return versionString;
    }

    equals(item: WineryVersion) {
        return this.componentVersion === item.componentVersion
            && this.wineryVersion === item.wineryVersion
            && this.workInProgressVersion === item.workInProgressVersion;
    }

    public compareTo(o: WineryVersion): number {
        if (isNullOrUndefined(o)) {
            return 1;
        }

        if (this.componentVersion === o.componentVersion) {
            if (this.componentVersion < o.componentVersion) {
                return -1;
            } else if (this.componentVersion > o.componentVersion) {
                return 1;
            }
        }

        if (this.wineryVersion < o.wineryVersion) {
            return -1;
        } else if (this.wineryVersion > o.wineryVersion) {
            return 1;
        }

        if (this.workInProgressVersion === 0) {
            return 1;
        } else if (o.workInProgressVersion === 0) {
            return -1;
        }

        if (this.workInProgressVersion < o.workInProgressVersion) {
            return -1;
        } else if (this.workInProgressVersion > o.workInProgressVersion) {
            return 1;
        }

        return 0;
    }
}
