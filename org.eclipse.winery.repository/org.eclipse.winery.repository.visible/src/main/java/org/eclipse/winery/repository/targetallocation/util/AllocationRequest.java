/*******************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.targetallocation.util;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class AllocationRequest {

    private boolean assignOnly;
    private int outputCap;
    private List<CriteriaRequest> selectedCriteria;

    public boolean isAssignOnly() {
        return assignOnly;
    }

    public int getOutputCap() {
        return outputCap;
    }

    public List<CriteriaRequest> getSelectedCriteria() {
        return selectedCriteria;
    }

    public static class CriteriaRequest {
        private String criteria;
        private JsonNode criteriaParams;

        public String getCriteria() {
            return criteria;
        }

        public JsonNode getCriteriaParams() {
            return criteriaParams;
        }
    }
}
