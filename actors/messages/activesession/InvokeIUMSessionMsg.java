package com.rogers.ute.hhsessionsvc.actors.messages.activesession;

import akka.actor.ActorRef;

import java.util.Date;

public class InvokeIUMSessionMsg {

    private final String ctn;
    private final String subId;
    private final Date sessionStartTime;
    private final ActorRef cacheHandler;

    public InvokeIUMSessionMsg(String ctn, String subId, Date sessionStartTime,ActorRef cacheHandler) {
        this.ctn = ctn;
        this.subId = subId;
        this.sessionStartTime = sessionStartTime;
        this.cacheHandler = cacheHandler;
    }

    public String getCtn() {
        return ctn;
    }

    public String getSubId() {
        return subId;
    }

    public Date getSessionStartTime() { return sessionStartTime; }

    public ActorRef getCacheHandler() { return cacheHandler; }
}
