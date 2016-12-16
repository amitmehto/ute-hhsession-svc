package com.rogers.ute.hhsessionsvc.models.ium;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IUMSessionRequest {

    @JsonProperty("getHHUsage")
    private GetHHUsage getHHUsage;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Constructors
     */
    public IUMSessionRequest() {}

    public IUMSessionRequest(GetHHUsage getHHUsage) {
        this.getHHUsage = getHHUsage;
    }

    /**
     *
     * @return
     * The getHHUsage
     */
    @JsonProperty("getHHUsage")
    public GetHHUsage getGetHHUsage() {
        return getHHUsage;
    }

    /**
     *
     * @param getHHUsage
     * The getHHUsage
     */
    @JsonProperty("getHHUsage")
    public void setGetHHUsage(GetHHUsage getHHUsage) {
        this.getHHUsage = getHHUsage;
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
