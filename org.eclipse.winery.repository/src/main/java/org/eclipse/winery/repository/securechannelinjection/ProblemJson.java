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

package org.eclipse.winery.repository.securechannelinjection;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProblemJson {

    private List<Problem> problemOccurrences;

    public List<Problem> getProblemOccurrences() {
        return problemOccurrences;
    }

    public void setProblemOccurrences(List<Problem> problemOccurrences) {
        this.problemOccurrences = problemOccurrences;
    }

    public static class Problem {
        private String problem;
        private String pattern;
        private String description;
        private List<Components> findings;

        public String getProblem() {
            return problem;
        }

        public void setProblem(String problem) {
            this.problem = problem;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Components> getFindings() {
            return findings;
        }

        public void setFindings(List<Components> findings) {
            this.findings = findings;
        }
    }

    public static class Components {

        @JsonProperty("Component_1")
        private String component1;

        @JsonProperty("Component_2")
        private String component2;

        public String getComponent1() {
            return component1;
        }

        public void setComponent1(String component1) {
            this.component1 = component1;
        }

        public String getComponent2() {
            return component2;
        }

        public void setComponent2(String component2) {
            this.component2 = component2;
        }
    }
}
