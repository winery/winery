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

import java.util.EnumSet;
import java.util.Objects;

import org.eclipse.winery.repository.backend.IRepository;

import org.eclipse.jdt.annotation.NonNull;

public class ConsistencyCheckerConfiguration {

    private final boolean testMode;
    private boolean serviceTemplatesOnly;
    private boolean checkDocumentation;
    private EnumSet<ConsistencyCheckerVerbosity> verbosity;
    private IRepository repository;

    public ConsistencyCheckerConfiguration(boolean serviceTemplatesOnly, boolean checkDocumentation, @NonNull EnumSet<ConsistencyCheckerVerbosity> verbosity, @NonNull IRepository repository) {
        this.serviceTemplatesOnly = serviceTemplatesOnly;
        this.checkDocumentation = checkDocumentation;
        this.verbosity = Objects.requireNonNull(verbosity);
        this.repository = Objects.requireNonNull(repository);
        this.testMode = false;
    }

    /**
     * Constructor used for testing the ConsistencyChecker functionality. Otherwise: Must not be used
     */
    public ConsistencyCheckerConfiguration(boolean serviceTemplatesOnly, boolean checkDocumentation, @NonNull EnumSet<ConsistencyCheckerVerbosity> verbosity, @NonNull IRepository repository, boolean testMode) {
        this.serviceTemplatesOnly = serviceTemplatesOnly;
        this.checkDocumentation = checkDocumentation;
        this.verbosity = Objects.requireNonNull(verbosity);
        this.repository = Objects.requireNonNull(repository);
        this.testMode = testMode;
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

    public boolean isTestMode() {
        return this.testMode;
    }
}
