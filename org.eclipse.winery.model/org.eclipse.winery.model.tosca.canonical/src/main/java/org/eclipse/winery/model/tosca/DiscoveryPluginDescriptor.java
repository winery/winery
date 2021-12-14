package org.eclipse.winery.model.tosca;

import java.util.List;

public class DiscoveryPluginDescriptor {
    private String id;
    private List<String> discoveredIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getDiscoveredIds() {
        return discoveredIds;
    }

    public void setDiscoveredIds(List<String> discoveredIds) {
        this.discoveredIds = discoveredIds;
    }
}
