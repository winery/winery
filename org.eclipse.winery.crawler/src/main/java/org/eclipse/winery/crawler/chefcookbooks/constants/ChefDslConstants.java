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

public class ChefDslConstants {
    public static final String SUPPORTSALLPLATFORMVERSIONS = ">= 0.0.0";

    public static final String SUPPORTSALLCOOKBOOKVERSIONS = ">= 0.0.0";

    // Chef Fields in Metadata
    public static final String COOKBOOK_NAME = "name";
    public static final String COOKBOOK_DESCRIPTION = "description";
    public static final String COOKBOOK_SUPPORTS = "supports";
    public static final String COOKBOOK_DEPENDS = "depends";
    public static final String COOKBOOK_VERSION = "version";
    
    public static final String OPENSUSELEAP = "opensuseleap";
    
    
    public static final String NODE = "node";

    // Package Resource 
    public static final String PACKAGE = "package";
    public static final String PACKAGE_NAME_PROPERTY = "package_name";
    public static final String PACKAGE_VERSION_PROPERTY = "version";
    public static final String PACKAGE_ACTION_PROPERTY = "action";
    
    public static final String INCLUDE_RECIPE = "include_recipe";
    
    // Paths in cookbook
    public static final String METADATA_RB_PATH = "/metadata.rb";
    public static final String METADATA_JSON_PATH = "/metadata.json";
    public static final String ATTRIBUTES_PATH = "/attributes";
    public static final String RECIPES_PATH = "/recipes";
    public static final String DEFAULT_RB_PATH = "/default.rb";
    
    public static final String DEFAULT_RUBYFILE = "default.rb";
    
}
