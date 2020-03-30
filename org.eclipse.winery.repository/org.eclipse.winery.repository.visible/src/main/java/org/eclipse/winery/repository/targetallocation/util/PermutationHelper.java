/********************************************************************************
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
 ********************************************************************************/

package org.eclipse.winery.repository.targetallocation.util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.HasId;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

/**
 * Helper class to use with {@link AllocationUtils#getPermutations(List, int)}.
 */
public class PermutationHelper {

    private List<TTopologyTemplate> replacements;
    private List<TNodeTemplate> correspondingNTs;
    private String targetLabel;

    public PermutationHelper(TNodeTemplate correspondingNT, TTopologyTemplate replacement) {
        this.replacements = Collections.singletonList(replacement);
        this.correspondingNTs = Collections.singletonList(correspondingNT);
    }

    public PermutationHelper(TNodeTemplate correspondingNT, String targetLabel) {
        this.correspondingNTs = Collections.singletonList(correspondingNT);
        this.targetLabel = targetLabel;
    }

    public PermutationHelper(List<TNodeTemplate> correspondingNTs, String targetLabel) {
        this.correspondingNTs = correspondingNTs;
        this.targetLabel = targetLabel;
    }

    public PermutationHelper(TNodeTemplate correspondingNT, List<TTopologyTemplate> replacements) {
        this.correspondingNTs = Collections.singletonList(correspondingNT);
        this.replacements = replacements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermutationHelper that = (PermutationHelper) o;
        return Objects.equals(correspondingNTs, that.correspondingNTs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(correspondingNTs);
    }

    @Override
    public String toString() {
        List<String> ids = correspondingNTs.stream().map(HasId::getId).collect(Collectors.toList());
        List<String> replacementIds = replacements.stream()
            .map(tt -> tt.getNodeTemplates().get(0).getId()).collect(Collectors.toList());
        return "{NTs: " + ids +
            ", replacement: " + replacementIds +
            ", target label: " + targetLabel + "}";
    }

    public TTopologyTemplate getReplacement() {
        return replacements.get(0);
    }

    public void setReplacement(TTopologyTemplate replacement) {
        this.replacements = Collections.singletonList(replacement);
    }

    public List<TTopologyTemplate> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<TTopologyTemplate> replacements) {
        this.replacements = replacements;
    }

    public TNodeTemplate getCorrespondingNT() {
        return correspondingNTs.get(0);
    }

    public void setCorrespondingNT(TNodeTemplate correspondingNT) {
        this.correspondingNTs = Collections.singletonList(correspondingNT);
    }

    public List<TNodeTemplate> getCorrespondingNTs() {
        return correspondingNTs;
    }

    public void setCorrespondingNTs(List<TNodeTemplate> correspondingNTs) {
        this.correspondingNTs = correspondingNTs;
    }

    public String getTargetLabel() {
        return targetLabel;
    }

    public void setTargetLabel(String targetLabel) {
        this.targetLabel = targetLabel;
    }
}
