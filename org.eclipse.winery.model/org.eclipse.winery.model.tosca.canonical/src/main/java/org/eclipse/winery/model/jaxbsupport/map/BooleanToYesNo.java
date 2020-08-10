/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.jaxbsupport.map;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.winery.model.tosca.constants.Namespaces;

public class BooleanToYesNo extends XmlAdapter<BooleanToYesNo.YesNo, Boolean> {
    @Override
    public Boolean unmarshal(YesNo yesNo) throws Exception {
        return yesNo == YesNo.YES;
    }

    @Override
    public YesNo marshal(Boolean aBoolean) throws Exception {
        return aBoolean ? YesNo.YES : YesNo.NO;
    }

    @XmlEnum()
    @XmlType(name = "yesNo", namespace = Namespaces.TOSCA_NAMESPACE)
    public static enum YesNo {
        @XmlEnumValue("yes")
        YES,
        @XmlEnumValue("no")
        NO
    }
}
