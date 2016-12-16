package com.rogers.ute.hhsessionsvc.actors.messages.activesession;

public class SessionCacheUpdateMsg {

    private final String subId;
    private final String ctn;
    private final String responseToCache;
    private final long currentTimeInMs;

    public SessionCacheUpdateMsg(String subId, String ctn, String responseToCache) {
        this.subId = subId;
        this.ctn = ctn;
        this.responseToCache = responseToCache;
        this.currentTimeInMs = System.currentTimeMillis();
    }

    public String getSubId() { return subId; }

    public String getCtn() { return ctn; }

    public String getResponseToCache() {
        return responseToCache;
    }

    public long getCurrentTimeInMs() { return currentTimeInMs; }
}
