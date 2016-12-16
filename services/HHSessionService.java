package com.rogers.ute.hhsessionsvc.services;


import akka.actor.ActorRef;
import akka.pattern.Patterns;
import com.rogers.ute.hhsessionsvc.actors.messages.activation.ActivationCacheCheckMsg;
import com.rogers.ute.hhsessionsvc.actors.messages.activesession.SessionCacheCheckMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Future;

public class HHSessionService {

    private final Logger logger = LoggerFactory.getLogger(HHSessionService.class);

    private final ActorRef iumCacheHandler;


    public HHSessionService(ActorRef iumCacheHandler) {
        this.iumCacheHandler = iumCacheHandler;
    }

    public Future activateSession(String subId, String sessionToken) {
        return Patterns.ask(iumCacheHandler, new ActivationCacheCheckMsg(subId, sessionToken), 10000);
    }

    public Future getActiveSessionInfo(String subId, String sessionToken, String ifModifiedSince) {
        return Patterns.ask(iumCacheHandler, new SessionCacheCheckMsg(subId, sessionToken, ifModifiedSince), 10000);
    }

}
