package com.rogers.ute.hhsessionsvc.models.ium;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IUMSessionResponse {

    @JsonProperty("getHHUsageResponse")
    private GetHHUsageResponse getHHUsageResponse;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The getHHUsageResponse
     */
    @JsonProperty("getHHUsageResponse")
    public GetHHUsageResponse getGetHHUsageResponse() {
        return getHHUsageResponse;
    }

    /**
     *
     * @param getHHUsageResponse
     * The getHHUsageResponse
     */
    @JsonProperty("getHHUsageResponse")
    public void setGetHHUsageResponse(GetHHUsageResponse getHHUsageResponse) {
        this.getHHUsageResponse = getHHUsageResponse;
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
