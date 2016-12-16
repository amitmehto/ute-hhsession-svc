package com.rogers.ute.hhsessionsvc.models.hhsessionresponses;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivationResponse {

    @JsonProperty("resultcode")
    private String resultCode;
    @JsonProperty("resultDesc")
    private String resultDesc;
    @JsonProperty("hhsessionId")
    private String hhSessionId;
    @JsonProperty("startTime")
    private String startTime;
    @JsonProperty("maxTime")
    private String maxTime;
    @JsonProperty("dataBucketSize")
    private String dataBucketSize;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public String getHhSessionId() {
        return hhSessionId;
    }

    public void setHhSessionId(String hhSessionId) {
        this.hhSessionId = hhSessionId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(String maxTime) {
        this.maxTime = maxTime;
    }

    public String getDataBucketSize() {
        return dataBucketSize;
    }

    public void setDataBucketSize(String dataBucketSize) {
        this.dataBucketSize = dataBucketSize;
    }
}
