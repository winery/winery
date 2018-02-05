/*******************************************************************************
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
 *******************************************************************************/
package org.eclipse.winery.model.tosca;

/**
 * Ensures that all inheritance things are available. Furthermore, it ensures that getDerivedFrom() returns HasType
 */
public interface HasInheritance {

    public TBoolean getAbstract();

    public void setAbstract(TBoolean value);

    public TBoolean getFinal();

    public void setFinal(TBoolean value);

    public HasType getDerivedFrom();

    public void setDerivedFrom(HasType value);
}
