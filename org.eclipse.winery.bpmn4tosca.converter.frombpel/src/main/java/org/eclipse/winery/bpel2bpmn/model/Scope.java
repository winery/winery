/******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Alex Frank - initial API and implementation
 ******************************************************************************/

package org.eclipse.winery.bpel2bpmn.model;

import org.eclipse.winery.bpmn2bpel.model.ManagementTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple Scope element which is similar to {@link org.eclipse.winery.bpel2bpmn.model.gen.TScope
 * but has useful utility methods.
 */
public class Scope {
    /**
     * The name of the Scope.
     */
    private String scopeName;
    /**
     * Sources of the Scope
     */
    private List<String> sources = new ArrayList<>();
    /**
     * Targets of the Scope
     */
    private List<String> targets = new ArrayList<>();
    /**
     * A list of {@link ManagementTask}
     */
    private List<ManagementTask> managementTasks = new ArrayList<>();

    /**
     * The name of the scope
     *
     * @return The scope name or null
     */
    public String getScopeName() {
        return scopeName;
    }

    /**
     * Sets the scope name
     *
     * @param scopeName - The scope name
     */
    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    /**
     * Returns the list of sources
     *
     * @return List of sources
     */
    public List<String> getSources() {
        return sources;
    }

    /**
     * Sets the sources list
     *
     * @param sources The sources list
     */
    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    /**
     * Returns the list of targets
     *
     * @return List of targets
     */
    public List<String> getTargets() {
        return targets;
    }

    /**
     * Sets the target list
     *
     * @param targets List of targets
     */
    public void setTargets(List<String> targets) {
        this.targets = targets;
    }

    /**
     * Returns the List of {@link ManagementTask}
     *
     * @return List of {@link ManagementTask}
     */
    public List<ManagementTask> getManagementTasks() {
        return managementTasks;
    }

    /**
     * Sets the ManagementTask List
     *
     * @param managementTasks The list of {@link ManagementTask}
     */
    public void setManagementTasks(List<ManagementTask> managementTasks) {
        this.managementTasks = managementTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Scope scope = (Scope) o;

        if (scopeName != null ? !scopeName.equals(scope.scopeName) : scope.scopeName != null) return false;
        if (sources != null ? !sources.equals(scope.sources) : scope.sources != null) return false;
        if (targets != null ? !targets.equals(scope.targets) : scope.targets != null) return false;
        return managementTasks != null ? managementTasks.equals(scope.managementTasks) : scope.managementTasks == null;
    }

    @Override
    public int hashCode() {
        int result = scopeName != null ? scopeName.hashCode() : 0;
        result = 31 * result + (sources != null ? sources.hashCode() : 0);
        result = 31 * result + (targets != null ? targets.hashCode() : 0);
        result = 31 * result + (managementTasks != null ? managementTasks.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Scope{" +
            "scopeName='" + scopeName + '\'' +
            ", sources=" + sources +
            ", targets=" + targets +
            ", managementTasks=" + managementTasks +
            '}';
    }
}
