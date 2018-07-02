/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.backend.consistencycheck;

import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import java.util.EnumSet;

public class ConsistencyCheckerConfiguration {

    private boolean serviceTemplatesOnly;
    private boolean checkDocumentation;
    private EnumSet<ConsistencyCheckerVerbosity> verbosity;
    private IRepository repository;

    public ConsistencyCheckerConfiguration() {
        this.serviceTemplatesOnly = false;
        this.checkDocumentation = false;
        this.verbosity = EnumSet.of(ConsistencyCheckerVerbosity.NONE);
        this.repository = RepositoryFactory.getRepository();
    }

    public ConsistencyCheckerConfiguration(boolean serviceTemplatesOnly, boolean checkDocumentation, EnumSet<ConsistencyCheckerVerbosity> verbosity, IRepository repository) {
        this.serviceTemplatesOnly = serviceTemplatesOnly;
        this.checkDocumentation = checkDocumentation;
        this.verbosity = verbosity;
        this.repository = repository;
    }

    public boolean isServiceTemplatesOnly() {
        return serviceTemplatesOnly;
    }

    public void setServiceTemplatesOnly(boolean serviceTemplatesOnly) {
        this.serviceTemplatesOnly = serviceTemplatesOnly;
    }

    public boolean isCheckDocumentation() {
        return checkDocumentation;
    }

    public void setCheckDocumentation(boolean checkDocumentation) {
        this.checkDocumentation = checkDocumentation;
    }

    public EnumSet<ConsistencyCheckerVerbosity> getVerbosity() {
        return verbosity;
    }

    public void setVerbosity(EnumSet<ConsistencyCheckerVerbosity> verbosity) {
        this.verbosity = verbosity;
    }

    public IRepository getRepository() {
        return repository;
    }

    public void setRepository(IRepository repository) {
        this.repository = repository;
    }
}
