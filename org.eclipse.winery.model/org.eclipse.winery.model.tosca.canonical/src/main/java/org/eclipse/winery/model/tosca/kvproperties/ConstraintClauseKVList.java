/*******************************************************************************
<<<<<<< HEAD
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
=======
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
>>>>>>> ccb0204fc... Pull the Id system from winery.common into the canonical model
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

package org.eclipse.winery.model.tosca.kvproperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ConstraintDefinitions")
public class ConstraintClauseKVList extends ArrayList<ConstraintClauseKV> implements Serializable {
    
    @XmlElement(name = "ConstraintDefinition")
    public List<ConstraintClauseKV> getConstraintDefinitionKVs() {
        return this;
    }

}
