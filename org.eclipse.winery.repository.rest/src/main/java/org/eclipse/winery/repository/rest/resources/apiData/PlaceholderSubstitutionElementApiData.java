/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.apiData;

import java.util.List;

import org.eclipse.winery.model.adaptation.substitution.refinement.RefinementCandidate;
import org.eclipse.winery.model.adaptation.substitution.refinement.placeholder.PlaceholderSubstitutionCandidate;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

public class PlaceholderSubstitutionElementApiData {

    public List<PlaceholderSubstitutionCandidate> substitutionCandidates;
    public ServiceTemplateId serviceTemplateContainingSubstitution;
    public TTopologyTemplate currentTopology;
    public int status;

    public PlaceholderSubstitutionElementApiData() {
    }

    public PlaceholderSubstitutionElementApiData(List<PlaceholderSubstitutionCandidate> candidates, ServiceTemplateId serviceTemplateContainingSubstitution, TTopologyTemplate currentTopology, int status) {
        this.substitutionCandidates = candidates;
        this.serviceTemplateContainingSubstitution = serviceTemplateContainingSubstitution;
        this.currentTopology = currentTopology;
        this.status = status;
    }
}
