package com.rogers.ute.hhsessionsvc.models.bas;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BASContent {

    @JsonProperty("id")
    private String id;
    @JsonProperty("number")
    private String number;
    @JsonProperty("status")
    private String status;
    @JsonProperty("type")
    private String type;
    @JsonProperty("serviceAccountNumber")
    private String serviceAccountNumber;
    @JsonProperty("subscriberInfo")
    private BASSubscriberInfo subscriberInfo;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The number
     */
    @JsonProperty("number")
    public String getNumber() {
        return number;
    }

    /**
     *
     * @param number
     * The number
     */
    @JsonProperty("number")
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     *
     * @return
     * The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The serviceAccountNumber
     */
    @JsonProperty("serviceAccountNumber")
    public String getServiceAccountNumber() {
        return serviceAccountNumber;
    }

    /**
     *
     * @param serviceAccountNumber
     * The serviceAccountNumber
     */
    @JsonProperty("serviceAccountNumber")
    public void setServiceAccountNumber(String serviceAccountNumber) {
        this.serviceAccountNumber = serviceAccountNumber;
    }

    /**
     *
     * @return
     * The subscriberInfo
     */
    @JsonProperty("subscriberInfo")
    public BASSubscriberInfo getSubscriberInfo() {
        return subscriberInfo;
    }

    /**
     *
     * @param subscriberInfo
     * The subscriberInfo
     */
    @JsonProperty("subscriberInfo")
    public void setSubscriberInfo(BASSubscriberInfo subscriberInfo) {
        this.subscriberInfo = subscriberInfo;
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
