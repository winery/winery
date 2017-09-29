/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
export enum LicenseEnum {
    None      = 'None',
    APL2      = 'Apache License 2.0',
    GPL3      = 'GNU General Public License v3.0',
    MIT       = 'MIT License',
    BSD2      = 'BSD 2-clause "Simplified" License',
    BSD3      = 'BSD 3-clause "New" or "Revised" License',
    EPL1      = 'Eclipse Public License 1.0',
    EPL2      = 'Eclipse Public License 2.0',
    AGPL3     = 'GNU Affero General Public License v3.0',
    GPL2      = 'GNU General Public License v2.0',
    LGPL21    = 'GNU Lesser General Public License v2.1',
    LGPL3     = 'GNU Lesser General Public License v3.0',
    MPL2      = 'Mozilla Public License 2.0',
    Unlicense = 'The Unlicense',
}

export class WineryLicense {

    public static getLicense(license: any) {
        let licenseText = '';

        switch (license) {
            case LicenseEnum.None:
                licenseText = '';
                break;
            case LicenseEnum.APL2:
                licenseText = 'SPDX:Apache-2.0';
                break;
            case LicenseEnum.GPL3:
                licenseText = 'SPDX:GPL-3.0';
                break;
            case LicenseEnum.MIT:
                licenseText = 'SPDX:MIT';
                break;
            case LicenseEnum.BSD2:
                licenseText = 'SPDX:BSD-2-Clause';
                break;
            case LicenseEnum.BSD3:
                licenseText = 'SPDX:BSD-3-Clause';
                break;
            case LicenseEnum.EPL1:
                licenseText = 'SPDX:EPL-1.0';
                break;
            case LicenseEnum.EPL2:
                licenseText = 'SPDX:EPL-2.0';
                break;
            case LicenseEnum.AGPL3:
                licenseText = 'SPDX:AGPL-3.0';
                break;
            case LicenseEnum.GPL2:
                licenseText = 'SPDX:GPL-2.0';
                break;
            case LicenseEnum.LGPL21:
                licenseText = 'SPDX:LGPL-2.1';
                break;
            case LicenseEnum.LGPL3:
                licenseText = 'SPDX:LGPL-3.0';
                break;
            case LicenseEnum.MPL2:
                licenseText = 'SPDX:MPL-2.0';
                break;
            case LicenseEnum.Unlicense:
                licenseText = 'SPDX:Unlicense';
                break;
            default:
                licenseText = 'error no valid license type';
                break;
        }
        return licenseText;
    }

}
