package org.eclipse.winery.model.tosca;

import java.util.List;
import java.util.Map;

public class ToscaDeploymentTechnology {
    private String id;
    private ToscaSouceTechnology sourceTechnology;
    private List<String> managedIds;
    private Map<String, String> properties;

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
}
