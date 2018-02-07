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

package org.eclipse.winery.repository.rest.resources._support.dataadapter;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Injections {

    @XmlElement
    private List<Injection> injection = new ArrayList<>();

    public List<Injection> getInjections() {
        return Collections.unmodifiableList(injection);
    }

    public void addInjection(Injection in) {
        injection.add(in);
    }
}
