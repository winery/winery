package org.eclipse.winery.repository.rest.resources._support.dataadapter.placementadapter;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tbpNode",
    "cpbNode",
    "matchId"
})
public class PlacementMatch {

    @JsonProperty("tbpNode")
    private TbpNode tbpNode;
    @JsonProperty("cpbNode")
    private CpbNode cpbNode;
    @JsonProperty("matchId")
    private String matchId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("tbpNode")
    public TbpNode getTbpNode() {
        return tbpNode;
    }

    @JsonProperty("tbpNode")
    public void setTbpNode(TbpNode tbpNode) {
        this.tbpNode = tbpNode;
    }

    @JsonProperty("cpbNode")
    public CpbNode getCpbNode() {
        return cpbNode;
    }

    @JsonProperty("cpbNode")
    public void setCpbNode(CpbNode cpbNode) {
        this.cpbNode = cpbNode;
    }

    @JsonProperty("matchId")
    public String getMatchId() {
        return matchId;
    }

    @JsonProperty("matchId")
    public void setMatchId(String matchId) {
        this.matchId = matchId;
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
