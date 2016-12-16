package com.rogers.ute.hhsessionsvc.models.ium;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IUMActivationRequest {

    @JsonProperty("activateHH")
    private ActivateHH activateHH;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Constructors
     */
    public IUMActivationRequest() {}

    public IUMActivationRequest(ActivateHH activateHH) {
        this.activateHH = activateHH;
    }


    /**
     *
     * @return
     * The activateHH
     */
    @JsonProperty("activateHH")
    public ActivateHH getActivateHH() {
        return activateHH;
    }

    /**
     *
     * @param activateHH
     * The activateHH
     */
    @JsonProperty("activateHH")
    public void setActivateHH(ActivateHH activateHH) {
        this.activateHH = activateHH;
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
