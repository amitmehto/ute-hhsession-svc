package com.rogers.ute.hhsessionsvc.actors.messages;

import akka.actor.ActorRef;

public class InvokeBASServiceMsg {

    private final String subId;
    private final String sessionToken;
    private final ActorRef iumInvoker;
    private final ActorRef cacheHandler;

    public InvokeBASServiceMsg(String subId, String sessionToken, ActorRef iumInvoker, ActorRef cacheHandler) {
        this.subId = subId;
        this.sessionToken = sessionToken;
        this.iumInvoker = iumInvoker;
        this.cacheHandler = cacheHandler;
    }

    @Override
    public String toString() {
        return "InvokeBASServiceMsg{" +
                "subId='" + subId + '\'' +
                '}';
    }

    public String getSubId() {
        return subId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public ActorRef getIumInvoker() { return iumInvoker; }

    public ActorRef getCacheHandler() { return cacheHandler; }
}
