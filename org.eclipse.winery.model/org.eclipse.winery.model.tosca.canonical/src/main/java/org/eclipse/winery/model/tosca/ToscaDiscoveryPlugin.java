package org.eclipse.winery.model.tosca;

import java.util.List;

public class ToscaDiscoveryPlugin {
    private String id;
    private ToscaSouceTechnology sourceTechnology;
    private List<String> discoveredIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ToscaSouceTechnology getSourceTechnology() {
        return sourceTechnology;
    }

    public void setSourceTechnology(ToscaSouceTechnology sourceTechnology) {
        this.sourceTechnology = sourceTechnology;
    }

    public List<String> getDiscoveredIds() {
        return discoveredIds;
    }

    public void setDiscoveredIds(List<String> discoveredIds) {
        this.discoveredIds = discoveredIds;
    }
}
