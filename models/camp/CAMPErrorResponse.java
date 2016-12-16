package com.rogers.ute.hhsessionsvc.models.camp;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CAMPErrorResponse {

    @JsonProperty("CAMP")
    private CampWrapper camp;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The camp
     */
    @JsonProperty("CAMP")
    public CampWrapper getCamp() {
        return camp;
    }

    /**
     *
     * @param camp
     * The CAMP
     */
    @JsonProperty("CAMP")
    public void setCamp(CampWrapper camp) {
        this.camp = camp;
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
