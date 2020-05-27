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
package org.eclipse.winery.model.version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.selector.ElementSelector;

import org.eclipse.winery.common.version.VersionState;
import org.eclipse.winery.common.version.VersionUtils;
import org.eclipse.winery.common.version.WineryVersion;
import org.eclipse.winery.model.tosca.HasIdInIdOrNameField;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.extensions.kvproperties.PropertyDefinitionKV;

import java.util.*;

public class ToscaDiff {

    private String element;
    private Object oldValue;
    private Object newValue;
    private VersionState state;
    private LinkedHashMap<String, ToscaDiff> childrenMap;

    private WineryVersion newVersion;
    private WineryVersion oldVersion;

    private ToscaDiff(String element) {
        this.element = element;
        this.childrenMap = new LinkedHashMap<>();
    }

    public String getElement() {
        return element;
    }

    public Object getOldValue() {
        return oldValue;
    }

    private void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    private void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public Collection<ToscaDiff> getChildren() {
        return Objects.nonNull(childrenMap) ? childrenMap.values() : null;
    }

    @JsonIgnore
    public LinkedHashMap<String, ToscaDiff> getChildrenMap() {
        return childrenMap;
    }

    private void addChild(List<ElementSelector> pathList, ToscaDiff diff) {
        String element = pathList.get(0).toHumanReadableString();

        if (pathList.size() > 1) {
            ToscaDiff parentDiff = this.childrenMap.containsKey(element) ? this.childrenMap.get(element) : new ToscaDiff(element);

            if (parentDiff.state != VersionState.ADDED && parentDiff.state != VersionState.REMOVED) {
                parentDiff.addChild(pathList.subList(1, pathList.size()), diff);
                this.childrenMap.put(element, parentDiff);
            }
        } else {
            this.childrenMap.put(element, diff);
        }
    }

    public VersionState getState() {
        return state;
    }

    private void setState(DiffNode.State state) {
        switch (state) {
            case ADDED:
                this.state = VersionState.ADDED;
                break;
            case CHANGED:
                this.state = VersionState.CHANGED;
                break;
            case REMOVED:
                this.state = VersionState.REMOVED;
                break;
            case UNTOUCHED:
                this.state = VersionState.UNCHANGED;
                break;
            default:
                this.state = null;
        }
    }

    @JsonIgnore
    public String getChangeLog() {
        LinkedHashMap<String, List<StringBuilder>> changeList = this.calculateChangeList(true);
        StringBuilder changeLog = new StringBuilder();

        if (Objects.nonNull(this.newVersion) && Objects.nonNull(this.oldVersion)) {
            changeLog.append("## Changes from version ")
                .append(oldVersion)
                .append(" to ")
                .append(newVersion);
        }

        if (Objects.nonNull(changeList.get(VersionState.ADDED.name()))) {
            if (changeLog.length() > 0) {
                changeLog.append("\n\n");
            }
            changeLog.append("### Added");
            changeList.get(VersionState.ADDED.name())
                .forEach(stringBuilder -> changeLog.append("\n- ").append(stringBuilder.toString()));
        }
        if (Objects.nonNull(changeList.get(VersionState.CHANGED.name()))) {
            if (changeLog.length() > 0) {
                changeLog.append("\n\n");
            }
            changeLog.append("### Changed");
            changeList.get(VersionState.CHANGED.name())
                .forEach(stringBuilder -> changeLog.append("\n- ").append(stringBuilder.toString()));
        }
        if (Objects.nonNull(changeList.get(VersionState.REMOVED.name()))) {
            if (changeLog.length() > 0) {
                changeLog.append("\n\n");
            }
            changeLog.append("### Removed");
            changeList.get(VersionState.REMOVED.name())
                .forEach(stringBuilder -> changeLog.append("\n- ").append(stringBuilder.toString()));
        }

        return changeLog.toString();
    }

    private LinkedHashMap<String, List<StringBuilder>> calculateChangeList(boolean isRoot) {
        LinkedHashMap<String, List<StringBuilder>> map = new LinkedHashMap<>();

        if (Objects.nonNull(childrenMap) && childrenMap.size() > 0) {
            for (ToscaDiff toscaDiff : getChildren()) {
                LinkedHashMap<String, List<StringBuilder>> childChanges = toscaDiff.calculateChangeList(false);

                addChangesToMap(VersionState.ADDED.name(), childChanges, map, !isRoot);
                addChangesToMap(VersionState.REMOVED.name(), childChanges, map, !isRoot);
                addChangesToMap(VersionState.CHANGED.name(), childChanges, map, !isRoot);
            }
        } else {
            List<StringBuilder> names = new ArrayList<>();
            StringBuilder builder = new StringBuilder(element);

            if (Objects.nonNull(newValue) || Objects.nonNull(oldValue)) {
                builder.append("\n  changed from \"")
                    .append(oldValue)
                    .append("\" to \"")
                    .append(newValue)
                    .append("\"");
            }

            names.add(builder);
            map.put(state.name(), names);
        }

        return map;
    }

    private void addChangesToMap(String key, LinkedHashMap<String, List<StringBuilder>> from,
                                 LinkedHashMap<String, List<StringBuilder>> map, boolean addSelfString) {
        if (Objects.nonNull(from.get(key))) {
            from.get(key).forEach(stringBuilder -> {
                List<StringBuilder> added = map.get(key);
                if (Objects.isNull(added)) {
                    added = new ArrayList<>();
                    map.put(key, added);
                }
                if (addSelfString) {
                    stringBuilder.insert(0, element + "/");
                }
                added.add(stringBuilder);
            });
        }
    }

    @Override
    @JsonIgnore
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToscaDiff toscaDiff = (ToscaDiff) o;
        return Objects.equals(element, toscaDiff.element);
    }

    @Override
    @JsonIgnore
    public int hashCode() {
        return Objects.hash(element, oldValue, newValue, state, childrenMap);
    }

    @JsonIgnore
    public static <T extends TExtensibleElements> ToscaDiff convertDiffToToscaDiff(DiffNode diffNode, T oldVersion, T newVersion) {
        ToscaDiff diff = new ToscaDiff(oldVersion.getClass().getSimpleName());

        if (oldVersion instanceof HasIdInIdOrNameField) {
            diff.oldVersion = VersionUtils.getVersion(((HasIdInIdOrNameField) oldVersion).getIdFromIdOrNameField());
        }
        if (newVersion instanceof HasIdInIdOrNameField) {
            diff.newVersion = VersionUtils.getVersion(((HasIdInIdOrNameField) newVersion).getIdFromIdOrNameField());
        }

        diffNode.visit((node, visit) -> {
            String itemName = node.getElementSelector().toHumanReadableString();

            if (itemName.startsWith("[")) {
                Object old = node.canonicalGet(oldVersion);
                Object newV = node.canonicalGet(newVersion);

                if (old instanceof HasIdInIdOrNameField) {
                    itemName = ((HasIdInIdOrNameField) old).getIdFromIdOrNameField();
                } else if (newV instanceof HasIdInIdOrNameField) {
                    itemName = ((HasIdInIdOrNameField) newV).getIdFromIdOrNameField();
                } else if (old instanceof PropertyDefinitionKV) {
                    itemName = ((PropertyDefinitionKV) old).getKey();
                } else if (newV instanceof PropertyDefinitionKV) {
                    itemName = ((PropertyDefinitionKV) newV).getKey();
                }
            }

            ToscaDiff childDiff = new ToscaDiff(itemName);
            List<ElementSelector> selectors = node.getPath().getElementSelectors();

            childDiff.setState(node.getState());
            if (node.hasChanges() && !node.hasChildren()) {
                childDiff.setNewValue(node.canonicalGet(newVersion));
                childDiff.setOldValue(node.canonicalGet(oldVersion));
            }

            if (selectors.size() > 1) {
                diff.addChild(selectors.subList(1, selectors.size()), childDiff);
            }
            if (node.isRootNode()) {
                diff.setState(node.getState());
            }
        });

        removeObjectAddresses(diff);

        return diff;
    }

    private static void removeObjectAddresses(ToscaDiff diff) {
        if (diff.childrenMap.size() > 0) {
            Iterator<Map.Entry<String, ToscaDiff>> iterator = diff.childrenMap.entrySet().iterator();
            Map.Entry<String, ToscaDiff> next = iterator.next();
            int counter = 0;

            // If one element starts with an "[", this element describes a list -> replace all with numbers
            if (next.getKey().startsWith("[")) {
                LinkedHashMap<String, ToscaDiff> map = new LinkedHashMap<>();
                do {
                    map.put(Integer.toString(counter++), diff.childrenMap.get(next.getKey()));
                    next = iterator.hasNext() ? iterator.next() : null;
                } while (Objects.nonNull(next));
                diff.childrenMap = map;
            }

            diff.childrenMap.forEach((key, child) -> {
                removeObjectAddresses(child);
            });
        } else {
            diff.childrenMap = null;
        }
    }
}
