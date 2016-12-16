package com.rogers.ute.hhsessionsvc.actors;

import akka.actor.*;
import akka.japi.Creator;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.FromConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.rogers.ute.hhsessionsvc.http.HttpClientProvider;
import com.rogers.ute.hhsessionsvc.helpers.CommonHelper;
import com.rogers.ute.hhsessionsvc.actors.messages.activesession.InvokeIUMSessionMsg;
import com.rogers.ute.hhsessionsvc.actors.messages.activesession.SessionCacheUpdateMsg;
import com.rogers.ute.hhsessionsvc.models.ium.GetHHUsage;
import com.rogers.ute.hhsessionsvc.models.ium.IUMSessionRequest;
import com.typesafe.config.Config;
import com.rogers.ute.hhsessionsvc.exceptions.HHSessionException;
import com.rogers.ute.hhsessionsvc.helpers.IUMHelper;
import com.rogers.ute.hhsessionsvc.actors.messages.activation.ActivationCacheUpdateMsg;
import com.rogers.ute.hhsessionsvc.actors.messages.activation.InvokeIUMActivateMsg;
import com.rogers.ute.hhsessionsvc.models.ium.ActivateHH;
import com.rogers.ute.hhsessionsvc.models.ium.IUMActivationRequest;
import play.libs.Json;

import java.util.concurrent.TimeUnit;

/**
 * Child actor that invokes IUM services for activation and session info
 */
public class IUMInvoker extends AbstractLoggingActor {

    private static final String NAME = "ium-invoker";
    // TODO - Fields used for request to IUMInvoker
    private static final String IUM_SERVICE_TYPE = "HH";
    private static final String IUM_CHANNEL_ID = "UTE";

    private final Config config;
    private final ObjectMapper mapper;
    private final AsyncHttpClient asyncHttpClient;

    /**
     * Actor instance creator
     * @param actorRefFactory
     * @param httpClientProvider
     * @return
     */
    public static ActorRef create(ActorRefFactory actorRefFactory, Config config, HttpClientProvider httpClientProvider) {
        Props props = Props.create(new Creator<Actor>() {
            private static final long serialVersionUID = 1L;

            @Override
            public IUMInvoker create() throws Exception {
                return new IUMInvoker(config, httpClientProvider);
            }
        });
        return actorRefFactory.actorOf(FromConfig.getInstance().props(props), NAME);
    }

    /**
     * Constructor
     */
    private IUMInvoker(Config config, HttpClientProvider httpClientProvider) {
        this.config = config;
        this.mapper = new ObjectMapper();
        this.asyncHttpClient = httpClientProvider.getHttpsClient();
        receive(ReceiveBuilder
                .match(InvokeIUMActivateMsg.class, this::callIUMActivate)
                .match(InvokeIUMSessionMsg.class, this::callIUMUsage)
                .build());
    }


    /**
     * Method that calls ium usage service, and fetches response
     * @param iumSessionMsg
     */
    private void callIUMUsage(InvokeIUMSessionMsg iumSessionMsg) {
        GetHHUsage innerData = new GetHHUsage(IUM_SERVICE_TYPE, IUMHelper.getTrxId(iumSessionMsg.getSubId()),
                iumSessionMsg.getCtn(), IUM_CHANNEL_ID);
        IUMSessionRequest request = new IUMSessionRequest(innerData);
        try {
            JsonNode jsonRequest = Json.toJson(request);
            ListenableFuture<Response> listenableFuture = asyncHttpClient.preparePost(config
                    .getString("endpoints.ium.url").concat(config
                            .getString("endpoints.ium.gethhusage")))
                    .setBody(jsonRequest.toString())
                    .setHeader("Content-Type","application/json")
                    .execute();
            String response = listenableFuture.get(Long.valueOf(config.getString("timeouts.ium.gethhusage")),
                    TimeUnit.SECONDS).getResponseBody(); // Blocking call
            getContext().sender().tell(IUMHelper.processIUMResponse
                    (IUMHelper.IUMTransactionType.HHActiveSession, response, iumSessionMsg.getSessionStartTime(),
                            mapper, config), self());
            iumSessionMsg.getCacheHandler().tell(new SessionCacheUpdateMsg(iumSessionMsg.getSubId(), iumSessionMsg.getCtn(),
                    response), self());
        } catch (Exception e) {
            if (e instanceof HHSessionException) {
                getContext().sender().tell(CommonHelper.getActivationResponseFromException(
                        (HHSessionException) e), self());
            } else {
                log().error("Unable to get IUM response. SUBID: " + iumSessionMsg
                        .getSubId());
                getContext().sender().tell(CommonHelper.getActivationResponseFromException(
                        new HHSessionException(config.getString("resultdescriptions.hh.systemerror"),
                                e)), self());
            }
        }
    }

    /**
     * Method that calls ium activate service, and fetches response
     * @param iumActivateMsg
     */
    private void callIUMActivate(InvokeIUMActivateMsg iumActivateMsg) {
        // Creating object which contains JSON request to IUM
        ActivateHH innerData = new ActivateHH(IUM_SERVICE_TYPE, IUMHelper.getTrxId(iumActivateMsg.getSubId()),
                iumActivateMsg.getCtn(), IUM_CHANNEL_ID);
        IUMActivationRequest request = new IUMActivationRequest(innerData);
        try {
            JsonNode jsonRequest = Json.toJson(request);
            String endpoint = config.getString("endpoints.ium.url")
                    .concat(config.getString("endpoints.ium.activatehh"));
            System.out.println(endpoint);
            ListenableFuture<Response> listenableFuture = asyncHttpClient.preparePost(endpoint)
                    .setBody(jsonRequest.toString())
                    .setHeader("Content-Type","application/json")
                    .execute();
            String response = listenableFuture.get(Long.valueOf(config.getString("timeouts.ium.hhactivate")),
                    TimeUnit.SECONDS).getResponseBody(); // Blocking call
            getContext().sender().tell(IUMHelper.processIUMResponse
                    (IUMHelper.IUMTransactionType.HHActivate, response, null, mapper, config), self());
            iumActivateMsg.getCacheHandler().tell(new ActivationCacheUpdateMsg(iumActivateMsg.getSubId(), iumActivateMsg.getCtn(),
                    response), self());
        } catch (Exception e) {
            if (e instanceof HHSessionException) {
                getContext().sender().tell(CommonHelper.getActivationResponseFromException(
                        (HHSessionException) e), self());
            } else {
                log().error("Unable to get IUM response. SUBID: " + iumActivateMsg
                        .getSubId());
                getContext().sender().tell(CommonHelper.getActivationResponseFromException(
                        new HHSessionException(config.getString("resultdescriptions.hh.systemerror"),
                                e)), self());
            }
        }
    }

}
