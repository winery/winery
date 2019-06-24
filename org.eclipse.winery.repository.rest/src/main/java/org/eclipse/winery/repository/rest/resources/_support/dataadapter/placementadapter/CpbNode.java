package org.eclipse.winery.repository.rest.resources._support.dataadapter.placementadapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "os_node",
    "node_type_of_os_node",
    "service_template_of_os_node",
    "csar_id_of_os_node",
    "caps_of_osnode",
    "instance_idof_osnode",
    "instance_idof_service_template_of_os_node",
    "property_map"
})
public class CpbNode {

    @JsonProperty("os_node")
    private String osNode;
    @JsonProperty("node_type_of_os_node")
    private String nodeTypeOfOsNode;
    @JsonProperty("service_template_of_os_node")
    private String serviceTemplateOfOsNode;
    @JsonProperty("csar_id_of_os_node")
    private String csarIdOfOsNode;
    @JsonProperty("caps_of_osnode")
    private List<String> capsOfOsnode = null;
    @JsonProperty("instance_idof_osnode")
    private Integer instanceIdofOsnode;
    @JsonProperty("instance_idof_service_template_of_os_node")
    private Integer instanceIdofServiceTemplateOfOsNode;
    @JsonProperty("property_map")
    private PropertyMap propertyMap;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("os_node")
    public String getOsNode() {
        return osNode;
    }

    @JsonProperty("os_node")
    public void setOsNode(String osNode) {
        this.osNode = osNode;
    }

    @JsonProperty("node_type_of_os_node")
    public String getNodeTypeOfOsNode() {
        return nodeTypeOfOsNode;
    }

    @JsonProperty("node_type_of_os_node")
    public void setNodeTypeOfOsNode(String nodeTypeOfOsNode) {
        this.nodeTypeOfOsNode = nodeTypeOfOsNode;
    }

    @JsonProperty("service_template_of_os_node")
    public String getServiceTemplateOfOsNode() {
        return serviceTemplateOfOsNode;
    }

    @JsonProperty("service_template_of_os_node")
    public void setServiceTemplateOfOsNode(String serviceTemplateOfOsNode) {
        this.serviceTemplateOfOsNode = serviceTemplateOfOsNode;
    }

    @JsonProperty("csar_id_of_os_node")
    public String getCsarIdOfOsNode() {
        return csarIdOfOsNode;
    }

    @JsonProperty("csar_id_of_os_node")
    public void setCsarIdOfOsNode(String csarIdOfOsNode) {
        this.csarIdOfOsNode = csarIdOfOsNode;
    }

    @JsonProperty("caps_of_osnode")
    public List<String> getCapsOfOsnode() {
        return capsOfOsnode;
    }

    @JsonProperty("caps_of_osnode")
    public void setCapsOfOsnode(List<String> capsOfOsnode) {
        this.capsOfOsnode = capsOfOsnode;
    }

    @JsonProperty("instance_idof_osnode")
    public Integer getInstanceIdofOsnode() {
        return instanceIdofOsnode;
    }

    @JsonProperty("instance_idof_osnode")
    public void setInstanceIdofOsnode(Integer instanceIdofOsnode) {
        this.instanceIdofOsnode = instanceIdofOsnode;
    }

    @JsonProperty("instance_idof_service_template_of_os_node")
    public Integer getInstanceIdofServiceTemplateOfOsNode() {
        return instanceIdofServiceTemplateOfOsNode;
    }

    @JsonProperty("instance_idof_service_template_of_os_node")
    public void setInstanceIdofServiceTemplateOfOsNode(Integer instanceIdofServiceTemplateOfOsNode) {
        this.instanceIdofServiceTemplateOfOsNode = instanceIdofServiceTemplateOfOsNode;
    }

    @JsonProperty("property_map")
    public PropertyMap getPropertyMap() {
        return propertyMap;
    }

    @JsonProperty("property_map")
    public void setPropertyMap(PropertyMap propertyMap) {
        this.propertyMap = propertyMap;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
