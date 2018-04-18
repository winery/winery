/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.datatypes;

import org.eclipse.winery.model.tosca.Definitions;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalNameForAngular {

    private final String id;
    private final String text;
    private final Definitions full;

    public LocalNameForAngular(String id, String text) {
        this(id, text, null);
    }

    public LocalNameForAngular(String id, String text, Definitions full) {
        this.id = id;
        this.text = text;
        this.full = full;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Definitions getFull() {
        return full;
    }
}
