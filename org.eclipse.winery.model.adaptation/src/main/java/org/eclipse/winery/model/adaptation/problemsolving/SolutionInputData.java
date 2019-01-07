/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.problemsolving;

import java.util.List;
import java.util.Map;

public class SolutionInputData {

    private String algorithm;
    private List<ComponentFinding> findings;
    private Map<String, String> algorithmSpecificInformation;

    public SolutionInputData() {
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public List<ComponentFinding> getFindings() {
        return findings;
    }

    public void setFindings(List<ComponentFinding> findings) {
        this.findings = findings;
    }

    public Map<String, String> getAlgorithmSpecificInformation() {
        return algorithmSpecificInformation;
    }

    public void setAlgorithmSpecificInformation(Map<String, String> algorithmSpecificInformation) {
        this.algorithmSpecificInformation = algorithmSpecificInformation;
    }
}
