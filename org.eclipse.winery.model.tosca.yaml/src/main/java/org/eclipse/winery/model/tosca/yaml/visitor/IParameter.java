/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.model.tosca.yaml.visitor;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

public interface IParameter<P> {
    String getKey();

    List<String> getContext();

    P addContext(String listName, String key);

    P addContext(String key);

    P addContext(List<String> context);

    @NonNull
    P copy();

    @NonNull
    P self();
}
