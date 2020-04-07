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

public class Defaults {

    public static final String COOKBOOK_PATH = System.getProperty("user.home") + "/cookbooks";
    public static final String COOKBOOKCONFIG_SUPPORTS_NO_PLATFORM = "no platform supported";

    // Temporary Folder for Crawler
    public static final String TEMP_FOLDER_PATH = System.getProperty("user.home") + "/temp";

    // Dependencies folder in cookbook
    public static final String DEPENDENCIE_FOLDER = "/dependencies";
}
