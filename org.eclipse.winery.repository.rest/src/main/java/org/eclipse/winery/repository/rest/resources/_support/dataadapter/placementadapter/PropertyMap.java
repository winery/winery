package org.eclipse.winery.repository.rest.resources._support.dataadapter.placementadapter;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PropertyMap {
    
    @JsonIgnore
    private Map<String, String> properties = new HashMap<String, String>();

    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, String value) {
        this.properties.put(name, value);
    }

}
