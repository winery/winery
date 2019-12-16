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

package org.eclipse.winery.crawler.chefcookbooks.constants;

public class OhaiFunctions {

    public static final String OHAI_PLATFORM_ATTRIBUTE_NAME = "['platform']";
    public static final String OHAI_PLATFORMFAMILY_ATTRIBUTE_NAME = "['platform_family']";
    public static final String OHAI_PLATFORMVERSION_ATTRIBUTE_NAME = "['platform_version']";

    public static String getPlatformFamilyFromPlatform(String platform) {
        String platform_family = null;

        switch (platform) {
            case "debian":
            case "ubuntu":
            case "linuxmint":
            case "raspbian":
            case "cumulus":
            case "kali":
                platform_family = "debian";
                break;
            case "oracle":
            case "centos":
            case "redhat":
            case "scientific":
            case "enterpriseenterprise":
            case "xcp":
            case "xenserver":
            case "cloudlinux":
            case "ibm_powerkvm":
            case "parallels":
            case "nexus_centos":
            case "clearos":
            case "bigip":
                platform_family = "rhel";
                break;
            case "amazon":
                platform_family = "amazon";
                break;
            case "suse":
            case "sles":
            case "opensuse":
            case "opensuseleap":
            case "sled":
                platform_family = "suse";
                break;
            case "fedora":
            case "pidora":
            case "arista_eos":
                platform_family = "fedora";
                break;
            case "nexus":
            case "ios_xr":
                platform_family = "wrlinux";
                break;
            case "gentoo":
                platform_family = "gentoo";
                break;
            case "slackware":
                platform_family = "slackware";
                break;
            case "arch":
            case "manjaro":
            case "antergos":
                platform_family = "arch";
                break;
            case "exherbo":
                platform_family = "exherbo";
                break;
            case "alpine":
                platform_family = "alpine";
                break;
            case "clearlinux":
                platform_family = "clearlinux";
                break;
            case "mangeia":
                platform_family = "mandriva";
                break;
            case "aix":
                platform_family = "aix";
                break;
            case "darwin":
            case "mac_os_x":
                platform_family = "mac_os_x";
                break;
            case "dragonflybsd":
                platform_family = "dragonflybsd";
                break;
            case "freebsd":
                platform_family = "freebsd";
                break;
            case "netbsd":
                platform_family = "netbsd";
                break;
            case "openbsd":
                platform_family = "openbsd";
                break;
            case "windows":
                platform_family = "windows";
                break;
            case "smartos":
                platform_family = "smartos";
                break;
            case "omnios":
                platform_family = "omnios";
                break;
            case "openindiana":
                platform_family = "openindiana";
                break;
            case "opensolaris":
                platform_family = "opensolaris";
                break;
            case "oraclesolaris":
                platform_family = "solaris2";
                break;
            case "solaris2":
                platform_family = "solaris2";
                break;
            case "nexentacore":
                platform_family = "nexentacore";
                break;
            case "zlinux":
                platform_family = "zlinux";
                break;
            default:
                break;
        }

        return platform_family;
    }
}
