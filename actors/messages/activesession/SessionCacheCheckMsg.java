package com.rogers.ute.hhsessionsvc.actors.messages.activesession;

public class SessionCacheCheckMsg {

    private final String subId;
    private final String sessionToken;
    private final String ifModifiedSince;

    public SessionCacheCheckMsg(String subId, String sessionToken, String ifModifiedSince) {
        this.subId = subId;
        this.sessionToken = sessionToken;
        this.ifModifiedSince = ifModifiedSince;
    }

    @Override
    public String toString() {
        return "ActivationCacheCheckMsg{" +
                "subid='" + subId + '\'' +
                '}';
    }

    public String getSubId() {
        return subId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public String getIfModifiedSince() { return ifModifiedSince; }
}
