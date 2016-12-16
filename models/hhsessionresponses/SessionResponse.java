package com.rogers.ute.hhsessionsvc.models.hhsessionresponses;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponse {

    @JsonProperty("resultcode")
    private String resultCode;
    @JsonProperty("hhsessionId")
    private String hhSessionId;
    @JsonProperty("startTime")
    private String startTime;
    @JsonProperty("maxTime")
    private String maxTime;
    @JsonProperty("dataBucketSize")
    private String dataBucketSize;
    @JsonProperty("dataConsumed")
    private String dataConsumed;
    @JsonProperty("elapsedTime")
    private String elapsedTime;
    @JsonIgnore
    private String lastModifiedTime;
    @JsonIgnore
    private boolean lastModifiedTimeValid;


    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
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

    public String getDataConsumed() {
        return dataConsumed;
    }

    public void setDataConsumed(String dataConsumed) {
        this.dataConsumed = dataConsumed;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public boolean isLastModifiedTimeValid() {
        return lastModifiedTimeValid;
    }

    public void setLastModifiedTimeValid(boolean lastModifiedTimeValid) {
        this.lastModifiedTimeValid = lastModifiedTimeValid;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
}
