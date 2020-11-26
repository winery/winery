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

package org.eclipse.winery.repository.rest.resources.apiData;

import java.util.Map;

import org.eclipse.winery.model.tosca.extensions.OTTopologyFragmentRefinementModel;

public class PermutationsResponse {

    private boolean mutable;
    private String error;
    private Map<String, OTTopologyFragmentRefinementModel> permutations;

    public boolean isMutable() {
        return mutable;
    }

    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setPermutations(Map<String, OTTopologyFragmentRefinementModel> permutations) {
        this.permutations = permutations;
    }

    public Map<String, OTTopologyFragmentRefinementModel> getPermutations() {
        return permutations;
    }
}
