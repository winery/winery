package org.eclipse.winery.model.tosca;

import java.util.List;
import java.util.Map;

public class DeploymentTechnologyDescriptor {
    private String id;
    private String technologyId;
    private List<String> managedIds;
    private List<String> discoveredIds;
    private Map<String, String> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTechnologyId() {
        return technologyId;
    }

    public void setTechnologyId(String technologyId) {
        this.technologyId = technologyId;
    }

    public List<String> getManagedIds() {
        return managedIds;
    }

    public void setManagedIds(List<String> managedIds) {
        this.managedIds = managedIds;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<String> getDiscoveredIds() {
        return discoveredIds;
    }

    public void setDiscoveredIds(List<String> discoveredIds) {
        this.discoveredIds = discoveredIds;
    }
}
