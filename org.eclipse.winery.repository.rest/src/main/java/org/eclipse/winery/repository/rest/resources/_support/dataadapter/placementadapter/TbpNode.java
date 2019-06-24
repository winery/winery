

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
    "to_be_placed_node",
    "node_type_of_to_be_placed_node",
    "service_template_of_to_be_placed_node",
    "csar_id_of_to_be_placed_node",
    "reqs_of_to_be_placed_node",
    "property_map"
})
public class TbpNode {

    @JsonProperty("to_be_placed_node")
    private String toBePlacedNode;
    @JsonProperty("node_type_of_to_be_placed_node")
    private String nodeTypeOfToBePlacedNode;
    @JsonProperty("service_template_of_to_be_placed_node")
    private String serviceTemplateOfToBePlacedNode;
    @JsonProperty("csar_id_of_to_be_placed_node")
    private String csarIdOfToBePlacedNode;
    @JsonProperty("reqs_of_to_be_placed_node")
    private List<String> reqsOfToBePlacedNode = null;
    @JsonProperty("property_map")
    private PropertyMap propertyMap;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("to_be_placed_node")
    public String getToBePlacedNode() {
        return toBePlacedNode;
    }

    @JsonProperty("to_be_placed_node")
    public void setToBePlacedNode(String toBePlacedNode) {
        this.toBePlacedNode = toBePlacedNode;
    }

    @JsonProperty("node_type_of_to_be_placed_node")
    public String getNodeTypeOfToBePlacedNode() {
        return nodeTypeOfToBePlacedNode;
    }

    @JsonProperty("node_type_of_to_be_placed_node")
    public void setNodeTypeOfToBePlacedNode(String nodeTypeOfToBePlacedNode) {
        this.nodeTypeOfToBePlacedNode = nodeTypeOfToBePlacedNode;
    }

    @JsonProperty("service_template_of_to_be_placed_node")
    public String getServiceTemplateOfToBePlacedNode() {
        return serviceTemplateOfToBePlacedNode;
    }

    @JsonProperty("service_template_of_to_be_placed_node")
    public void setServiceTemplateOfToBePlacedNode(String serviceTemplateOfToBePlacedNode) {
        this.serviceTemplateOfToBePlacedNode = serviceTemplateOfToBePlacedNode;
    }

    @JsonProperty("csar_id_of_to_be_placed_node")
    public String getCsarIdOfToBePlacedNode() {
        return csarIdOfToBePlacedNode;
    }

    @JsonProperty("csar_id_of_to_be_placed_node")
    public void setCsarIdOfToBePlacedNode(String csarIdOfToBePlacedNode) {
        this.csarIdOfToBePlacedNode = csarIdOfToBePlacedNode;
    }

    @JsonProperty("reqs_of_to_be_placed_node")
    public List<String> getReqsOfToBePlacedNode() {
        return reqsOfToBePlacedNode;
    }

    @JsonProperty("reqs_of_to_be_placed_node")
    public void setReqsOfToBePlacedNode(List<String> reqsOfToBePlacedNode) {
        this.reqsOfToBePlacedNode = reqsOfToBePlacedNode;
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
