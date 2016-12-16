package com.rogers.ute.hhsessionsvc.actors.messages.activation;

public class ActivationCacheCheckMsg {

    private final String subId;
    private final String sessionToken;

    public ActivationCacheCheckMsg(String subId, String sessionToken) {
        this.subId = subId;
        this.sessionToken = sessionToken;
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
}
