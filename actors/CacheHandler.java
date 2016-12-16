package com.rogers.ute.hhsessionsvc.actors;

import akka.actor.*;
import akka.japi.Creator;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.FromConfig;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Update;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rogers.ute.hhsessionsvc.dao.DatabaseClient;
import com.rogers.ute.hhsessionsvc.exceptions.HHSessionException;
import com.rogers.ute.hhsessionsvc.helpers.CommonHelper;
import com.rogers.ute.hhsessionsvc.helpers.IUMHelper;
import com.rogers.ute.hhsessionsvc.actors.messages.InvokeBASServiceMsg;
import com.rogers.ute.hhsessionsvc.actors.messages.activation.ActivationCacheCheckMsg;
import com.rogers.ute.hhsessionsvc.actors.messages.activation.ActivationCacheUpdateMsg;
import com.rogers.ute.hhsessionsvc.actors.messages.activesession.InvokeIUMSessionMsg;
import com.rogers.ute.hhsessionsvc.actors.messages.activesession.SessionCacheCheckMsg;
import com.rogers.ute.hhsessionsvc.actors.messages.activesession.SessionCacheUpdateMsg;
import com.rogers.ute.hhsessionsvc.models.hhsessionresponses.SessionResponse;
import com.typesafe.config.Config;
import play.libs.Json;

import java.util.Date;

/**
 * Actor that handles cache data for IUM activateHH & getHHusage responses
 */
public class CacheHandler extends AbstractLoggingActor {

    private static final String NAME = "cache-handler";

    private final Config config;
    private final ObjectMapper mapper;
    private final DatabaseClient dbClient;
    private final ActorRef basInvoker;
    private final ActorRef iumInvoker;


    /**
     * Actor instance creator
     * @param actorRefFactory
     * @param dbClient
     * @param basInvoker
     * @param iumInvoker
     * @return
     */
    public static ActorRef create(ActorRefFactory actorRefFactory, Config config, DatabaseClient dbClient,
                                  ActorRef basInvoker, ActorRef iumInvoker) {
        Props props = Props.create(new Creator<Actor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public CacheHandler create() throws Exception {
                return new CacheHandler(config, dbClient, basInvoker, iumInvoker);
            }
        });
        return actorRefFactory.actorOf(FromConfig.getInstance().props(props), NAME);
    }


    /**
     * Constructor
     * @param config
     * @param dbClient
     * @param basInvoker
     * @param iumInvoker
     */
    private CacheHandler(Config config, DatabaseClient dbClient, ActorRef basInvoker, ActorRef iumInvoker) {
        this.config = config;
        this.mapper = new ObjectMapper();
        this.dbClient = dbClient;
        this.basInvoker = basInvoker;
        this.iumInvoker = iumInvoker;
        receive(ReceiveBuilder
                .match(ActivationCacheCheckMsg.class, this::checkActivationCache)
                .match(ActivationCacheUpdateMsg.class, this::updateActivationCache)
                .match(SessionCacheCheckMsg.class, this::checkSessionCache)
                .match(SessionCacheUpdateMsg.class, this::updateSessionCache)
                .build());
    }


    /**
     * Method that checks cache for activation response
     * @param cacheCheckMsg
     */
    private void checkActivationCache(ActivationCacheCheckMsg cacheCheckMsg) {
        try {
            ResultSet resultSet = dbClient.executeAsync("Select * from " + config
                    .getString("cache.tables.iumsession") +
                    " where sub_id =" + cacheCheckMsg.getSubId() + ";");
            Row row = resultSet.one();
            String ctn = row.getString("ctn");
            if (null == ctn) {
                deleteRecord(cacheCheckMsg.getSubId());
            }
            String response = row.getString("ium_response");
            getContext().sender().tell(Json.toJson(IUMHelper.processIUMResponse
                    (IUMHelper.IUMTransactionType.HHActivate, response, null, mapper, config)), self());
        } catch (Exception e) {
            log().error(e.getMessage());
            basInvoker.forward(new InvokeBASServiceMsg(cacheCheckMsg.getSubId(), cacheCheckMsg.getSessionToken(),
                            iumInvoker, self()),
                    getContext());
        }
    }

    /**
     * Method that updates activation response in cache
     * @param iumActivationCacheUpdateMsg
     */
    private void updateActivationCache(ActivationCacheUpdateMsg iumActivationCacheUpdateMsg) {
        try {
            Insert insert = QueryBuilder.insertInto(config.getString("cache.tables.iumsession"));
            long currentTime = System.currentTimeMillis();
            insert.value("last_update_time", currentTime);
            insert.value("session_start_time", currentTime);
            insert.value("sub_id", QueryBuilder.raw(iumActivationCacheUpdateMsg.getSubId()));
            insert.value("ctn", QueryBuilder.raw("'"+iumActivationCacheUpdateMsg.getCtn()+"'"));
            insert.value("ium_response", QueryBuilder.raw("'"+iumActivationCacheUpdateMsg.getResponseToCache()+"'"));
            insert.using(QueryBuilder.ttl(Integer.valueOf(config.getString("cache.ttl.ium.activation"))));
            log().info(iumActivationCacheUpdateMsg.getSubId());
            log().info(insert.getQueryString());
            dbClient.executeAsync(insert.getQueryString());
            log().debug(new StringBuilder("Activation Cache updated for: ").append(iumActivationCacheUpdateMsg
                    .getSubId()).toString());
        } catch (Exception e) {
            log().error("Unable to update IUM Activation response in cache. SUBID: " + iumActivationCacheUpdateMsg
                    .getSubId() + ". Exception: " + e.getMessage());
        }
    }


    /**
     * Method that checks cache for session response
     * @param cacheCheckMsg
     */
    private void checkSessionCache(SessionCacheCheckMsg cacheCheckMsg) {
        try {
            log().debug("HELLO");
            ResultSet resultSet = dbClient.executeAsync("Select * from " + config
                    .getString("cache.tables.iumsession") +
                    " where sub_id =" + cacheCheckMsg.getSubId() + ";");
            Row row = resultSet.one();
            String ctn = row.getString("ctn");
            if (null == ctn) {
                deleteRecord(cacheCheckMsg.getSubId());
            }
            Date lastUpdateTime = row.getTimestamp("last_update_time");
            Date sessionStartTime = row.getTimestamp("session_start_time");
            if (CommonHelper.isTimeForCacheRefresh(lastUpdateTime, Integer.valueOf(config
                    .getString("cache.ttl.ium.session")))) {
                iumInvoker.forward(new InvokeIUMSessionMsg(ctn, cacheCheckMsg.getSubId(), sessionStartTime, self()),
                        getContext());
            } else {
                String response = row.getString("ium_response");
                SessionResponse sessionResponse = (SessionResponse) IUMHelper.processIUMResponse
                        (IUMHelper.IUMTransactionType.HHActiveSession, response, sessionStartTime, mapper, config);
                // TODO
                if (CommonHelper.isLastModifiedTimeValid(cacheCheckMsg.getIfModifiedSince(), lastUpdateTime)) {
                    sessionResponse.setLastModifiedTimeValid(true);
                }
                getContext().sender().tell(sessionResponse, self());
            }

        } catch (Exception e) {
            if (e instanceof HHSessionException) {
                log().error(((HHSessionException) e).getHhMessage());
            } else {
                log().error("Unable to get Session response from cache. SubID: " + cacheCheckMsg
                        .getSubId());
                log().error(e.getMessage());
            }
            getContext().sender().tell(CommonHelper.getSessionResponseFromException(
                    new HHSessionException(config.getString("resultcodes.hh.noactivesession"),
                            config.getString("resultdescriptions.hh.noactivesession"),
                            e)), self());
        }
    }


    /**
     * Method that updates session response in cache
     * @param sessionCacheUpdateMsg
     */
    private void updateSessionCache(SessionCacheUpdateMsg sessionCacheUpdateMsg) {
        try {
            Update update = QueryBuilder.update(config.getString("cache.tables.iumsession"));
            update.with(QueryBuilder.set("last_update_time", sessionCacheUpdateMsg.getCurrentTimeInMs()));
            update.with(QueryBuilder.set("ium_response", QueryBuilder.raw("'"+
                    sessionCacheUpdateMsg.getResponseToCache()+"'")));
            update.where(QueryBuilder.eq("sub_id", QueryBuilder.raw(sessionCacheUpdateMsg.getSubId())));
            log().info(sessionCacheUpdateMsg.getSubId());
            log().info(update.getQueryString());
            dbClient.executeAsync(update.getQueryString());
            log().debug(new StringBuilder("Session Cache updated for SUBID: ").append(sessionCacheUpdateMsg
                    .getSubId()).toString());
        } catch (Exception e) {
            log().error("Unable to update IUM Session response in cache. SUBID: " + sessionCacheUpdateMsg
                    .getSubId() + ". Exception: " + e.getMessage());
        }
    }


    /**
     * Method that deletes record from cache if ctn is null
     * @param subId
     * @throws HHSessionException
     */
    private void deleteRecord(String subId) throws HHSessionException {
        try {
            Delete delete = QueryBuilder.delete().from(config.getString("cache.tables.iumsession"));
            delete.where(QueryBuilder.in("sub_id", QueryBuilder.raw(subId)));
            log().info(delete.getQueryString());
            dbClient.executeAsync(delete.getQueryString());
        } catch (Exception e) {
            log().error("Unable to delete record from Cache. SUBID: " + subId);
            log().error(e.getMessage());
        }
        String msg = new StringBuilder("Session Cache removed for SUBID: ").append(subId).toString();
        throw new HHSessionException(msg);
    }

}
