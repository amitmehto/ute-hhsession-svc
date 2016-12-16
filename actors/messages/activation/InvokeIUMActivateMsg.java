package com.rogers.ute.hhsessionsvc.actors.messages.activation;

import akka.actor.ActorRef;

public class InvokeIUMActivateMsg {

    private final String ctn;
    private final String subId;
    private final ActorRef cacheHandler;

    public InvokeIUMActivateMsg(String ctn, String subId, ActorRef cacheHandler) {
        this.ctn = ctn;
        this.subId = subId;
        this.cacheHandler = cacheHandler;
    }

    public String getCtn() {
        return ctn;
    }

    public String getSubId() {
        return subId;
    }

    public ActorRef getCacheHandler() { return cacheHandler; }
}
