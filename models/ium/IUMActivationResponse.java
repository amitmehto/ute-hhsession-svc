package com.rogers.ute.hhsessionsvc.models.ium;


import com.fasterxml.jackson.annotation.*;

import java.util.Map;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IUMActivationResponse {

    @JsonProperty("activateHHResponse")
    private ActivateHHResponse activateHHResponse;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The activateHHResponse
     */
    @JsonProperty("activateHHResponse")
    public ActivateHHResponse getActivateHHResponse() {
        return activateHHResponse;
    }

    /**
     *
     * @param activateHHResponse
     * The activateHHResponse
     */
    @JsonProperty("activateHHResponse")
    public void setActivateHHResponse(ActivateHHResponse activateHHResponse) {
        this.activateHHResponse = activateHHResponse;
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
