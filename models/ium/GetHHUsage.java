package com.rogers.ute.hhsessionsvc.models.ium;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetHHUsage {

    @JsonProperty("serviceType")
    private String serviceType;
    @JsonProperty("TrxId")
    private String trxId;
    @JsonProperty("ctn")
    private String ctn;
    @JsonProperty("channelId")
    private String channelId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    /**
     * Constructors
     */
    public GetHHUsage() {}

    public GetHHUsage(String serviceType, String trxId, String ctn, String channelId) {
        this.serviceType = serviceType;
        this.trxId = trxId;
        this.ctn = ctn;
        this.channelId = channelId;
    }


    /**
     *
     * @return
     * The serviceType
     */
    @JsonProperty("serviceType")
    public String getServiceType() {
        return serviceType;
    }

    /**
     *
     * @param serviceType
     * The serviceType
     */
    @JsonProperty("serviceType")
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     *
     * @return
     * The trxId
     */
    @JsonProperty("TrxId")
    public String getTrxId() {
        return trxId;
    }

    /**
     *
     * @param trxId
     * The TrxId
     */
    @JsonProperty("TrxId")
    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    /**
     *
     * @return
     * The ctn
     */
    @JsonProperty("ctn")
    public String getCtn() {
        return ctn;
    }

    /**
     *
     * @param ctn
     * The ctn
     */
    @JsonProperty("ctn")
    public void setCtn(String ctn) {
        this.ctn = ctn;
    }

    /**
     *
     * @return
     * The channelId
     */
    @JsonProperty("channelId")
    public String getChannelId() {
        return channelId;
    }

    /**
     *
     * @param channelId
     * The channelId
     */
    @JsonProperty("channelId")
    public void setChannelId(String channelId) {
        this.channelId = channelId;
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
