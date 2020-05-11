/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.converter.support.validator.support;

import java.util.Objects;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractVisitor;
import org.eclipse.winery.repository.converter.support.exception.MultiException;

public abstract class ExceptionVisitor<R extends AbstractResult<R>, P extends AbstractParameter<P>> extends AbstractVisitor<R, P> {
    private MultiException exception;

    public MultiException getException() {
        return exception;
    }

    public void setException(Exception exception) {
        if (Objects.isNull(this.exception)) {
            this.exception = new MultiException();
        }
        this.exception.add(exception);
    }

    public boolean hasExceptions() {
        return Objects.nonNull(this.exception);
    }
}
