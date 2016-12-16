package com.rogers.ute.hhsessionsvc.actors.messages.activation;

public class ActivationCacheUpdateMsg {

    private final String subId;
    private final String ctn;
    private final String responseToCache;

    public ActivationCacheUpdateMsg(String subId, String ctn, String responseToCache) {
        this.subId = subId;
        this.ctn = ctn;
        this.responseToCache = responseToCache;
    }



    public String getSubId() { return subId; }

    public String getCtn() { return ctn; }

    public String getResponseToCache() {
        return responseToCache;
    }

}
