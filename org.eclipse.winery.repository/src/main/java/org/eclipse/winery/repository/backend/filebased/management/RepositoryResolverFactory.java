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

package org.eclipse.winery.repository.backend.filebased.management;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.eclipse.winery.common.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryResolverFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryResolverFactory.class);

    public static Optional<IRepositoryResolver> getResolver(String url) {
        return getResolver(url, Constants.MASTER_BRANCH);
    }

    public static Optional<IRepositoryResolver> getResolver(String url, String branch) {
        try {
            if (new URL(url).getHost().contains("github.com") || new URL(url).getHost().contains("gitlab")) {
                return Optional.of(new GitResolver(url, branch));
            }
        } catch (MalformedURLException e) {
            LOGGER.error("The repository host could not be determined.", e);
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
