/********************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.filebased.management;

import java.io.File;
import java.io.IOException;

import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;

import org.eclipse.jgit.api.errors.GitAPIException;

public interface IRepositoryResolver {

    String getVcsSystem();

    String getUrl();

    String getRepositoryMaintainerUrl();

    String getRepositoryMaintainer();

    String getRepositoryName();

    GitBasedRepository createRepository(File repositoryLocation) throws IOException, GitAPIException;
}
