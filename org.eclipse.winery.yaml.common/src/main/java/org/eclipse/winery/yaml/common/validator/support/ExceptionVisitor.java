/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.yaml.common.validator.support;

import java.util.Objects;

import org.eclipse.winery.model.tosca.yaml.visitor.AbstractParameter;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractVisitor;
import org.eclipse.winery.yaml.common.exception.MultiException;

public abstract class ExceptionVisitor<R extends AbstractResult<R>, P extends AbstractParameter<P>> extends AbstractVisitor<R, P> {
    private MultiException exception;

    public MultiException getException() {
        return exception;
    }

    public void setException(Exception exception) {
        if (!Objects.nonNull(this.exception)) {
            this.exception = new MultiException();
        }
        this.exception.add(exception);
    }

    public boolean hasExceptions() {
        return Objects.nonNull(this.exception);
    }
}
