package com.rogers.ute.hhsessionsvc.models.ium;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetHHUsageResponse {

    @JsonProperty("TrxId")
    private String trxId;
    @JsonProperty("channelId")
    private String channelId;
    @JsonProperty("ctn")
    private String ctn;
    @JsonProperty("hhConsumedUsage")
    private String hhConsumedUsage;
    @JsonProperty("hhIUMcurrentTime")
    private String hhIUMcurrentTime;
    @JsonProperty("hhSessionBucketSize")
    private String hhSessionBucketSize;
    @JsonProperty("hhSessionDuration")
    private String hhSessionDuration;
    @JsonProperty("hhSessionId")
    private String hhSessionId;
    @JsonProperty("hhSessionStartTime")
    private String hhSessionStartTime;
    @JsonProperty("resultCode")
    private String resultCode;
    @JsonProperty("resultDescription")
    private String resultDescription;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
     * The hhConsumedUsage
     */
    @JsonProperty("hhConsumedUsage")
    public String getHhConsumedUsage() {
        return hhConsumedUsage;
    }

    /**
     *
     * @param hhConsumedUsage
     * The hhConsumedUsage
     */
    @JsonProperty("hhConsumedUsage")
    public void setHhConsumedUsage(String hhConsumedUsage) {
        this.hhConsumedUsage = hhConsumedUsage;
    }

    /**
     *
     * @return
     * The hhIUMcurrentTime
     */
    @JsonProperty("hhIUMcurrentTime")
    public String getHhIUMcurrentTime() {
        return hhIUMcurrentTime;
    }

    /**
     *
     * @param hhIUMcurrentTime
     * The hhIUMcurrentTime
     */
    @JsonProperty("hhIUMcurrentTime")
    public void setHhIUMcurrentTime(String hhIUMcurrentTime) {
        this.hhIUMcurrentTime = hhIUMcurrentTime;
    }

    /**
     *
     * @return
     * The hhSessionBucketSize
     */
    @JsonProperty("hhSessionBucketSize")
    public String getHhSessionBucketSize() {
        return hhSessionBucketSize;
    }

    /**
     *
     * @param hhSessionBucketSize
     * The hhSessionBucketSize
     */
    @JsonProperty("hhSessionBucketSize")
    public void setHhSessionBucketSize(String hhSessionBucketSize) {
        this.hhSessionBucketSize = hhSessionBucketSize;
    }

    /**
     *
     * @return
     * The hhSessionDuration
     */
    @JsonProperty("hhSessionDuration")
    public String getHhSessionDuration() {
        return hhSessionDuration;
    }

    /**
     *
     * @param hhSessionDuration
     * The hhSessionDuration
     */
    @JsonProperty("hhSessionDuration")
    public void setHhSessionDuration(String hhSessionDuration) {
        this.hhSessionDuration = hhSessionDuration;
    }

    /**
     *
     * @return
     * The hhSessionId
     */
    @JsonProperty("hhSessionId")
    public String getHhSessionId() {
        return hhSessionId;
    }

    /**
     *
     * @param hhSessionId
     * The hhSessionId
     */
    @JsonProperty("hhSessionId")
    public void setHhSessionId(String hhSessionId) {
        this.hhSessionId = hhSessionId;
    }

    /**
     *
     * @return
     * The hhSessionStartTime
     */
    @JsonProperty("hhSessionStartTime")
    public String getHhSessionStartTime() {
        return hhSessionStartTime;
    }

    /**
     *
     * @param hhSessionStartTime
     * The hhSessionStartTime
     */
    @JsonProperty("hhSessionStartTime")
    public void setHhSessionStartTime(String hhSessionStartTime) {
        this.hhSessionStartTime = hhSessionStartTime;
    }

    /**
     *
     * @return
     * The resultCode
     */
    @JsonProperty("resultCode")
    public String getResultCode() {
        return resultCode;
    }

    /**
     *
     * @param resultCode
     * The resultCode
     */
    @JsonProperty("resultCode")
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    /**
     *
     * @return
     * The resultDescription
     */
    @JsonProperty("resultDescription")
    public String getResultDescription() {
        return resultDescription;
    }

    /**
     *
     * @param resultDescription
     * The resultDescription
     */
    @JsonProperty("resultDescription")
    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
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
