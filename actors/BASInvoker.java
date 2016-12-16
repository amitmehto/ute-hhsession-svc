package com.rogers.ute.hhsessionsvc.actors;

import akka.actor.*;
import akka.japi.Creator;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.FromConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;
import com.rogers.ute.hhsessionsvc.http.HttpClientProvider;
import com.rogers.ute.hhsessionsvc.helpers.CommonHelper;
import com.rogers.ute.hhsessionsvc.actors.messages.InvokeBASServiceMsg;
import com.typesafe.config.Config;
import com.rogers.ute.hhsessionsvc.exceptions.HHSessionException;
import com.rogers.ute.hhsessionsvc.actors.messages.activation.InvokeIUMActivateMsg;
import com.rogers.ute.hhsessionsvc.models.bas.BASContent;
import com.rogers.ute.hhsessionsvc.models.bas.BASSubscriberResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Child actor that invokes Billing Account Services(BAS) /subscriptions service
 */
public class BASInvoker extends AbstractLoggingActor {

    private static final String NAME = "bas-invoker";
    // To read from configuration file
    private final Config config;
    private final ObjectMapper mapper;
    private final AsyncHttpClient asyncHttpClient;
    // TODO - Need to configure OR read from Request
    private static final String X_APP_ID = "MyAccount";


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
            public BASInvoker create() throws Exception {
                return new BASInvoker(config, httpClientProvider);
            }
        });
        return actorRefFactory.actorOf(FromConfig.getInstance().props(props), NAME);
    }

    /**
     * Constructor
     * @param httpClientProvider
     */
    private BASInvoker(Config config, HttpClientProvider httpClientProvider) {
        this.config = config;
        this.mapper = new ObjectMapper();
        this.asyncHttpClient = httpClientProvider.getHttpClient();
        receive(ReceiveBuilder
                .match(InvokeBASServiceMsg.class, this::getBASResponse)
                .build());
    }


    /**
     * Method that calls billing account service, and fetches response
     * @param invokeBASServiceMsg
     */
    private void getBASResponse(InvokeBASServiceMsg invokeBASServiceMsg) {
        log().debug(invokeBASServiceMsg.toString());
        /*
        String endpoint = config.getString("endpoints.bas.url")
                .concat(invokeBASServiceMsg.getSubId())
                .concat(config.getString("endpoints.bas.subscriptions"));
        */
        String endpoint = config.getString("endpoints.bas.url").concat("028d44cc-ab49-4821-aeea-e369369a5a93").concat("/subscriptions");
        try {
            ListenableFuture<Response> listenableFuture = asyncHttpClient.prepareGet(endpoint)
                    .setHeader("x-session-token", invokeBASServiceMsg.getSessionToken())
                    .setHeader("x-app-id", X_APP_ID)
                    .execute();
            String response = listenableFuture.get(Long.valueOf(config
                            .getString("timeouts.bas.subscriber")),
                    TimeUnit.SECONDS).getResponseBody(); // Potential blocking call
            InvokeIUMActivateMsg iumActivateMsg = getMsgToInvokeIUM(response, invokeBASServiceMsg);
            invokeBASServiceMsg.getIumInvoker().forward(iumActivateMsg, getContext());
        } catch (Exception e) {
            if (e instanceof HHSessionException) {
                getContext().sender().tell(CommonHelper.getActivationResponseFromException(
                        (HHSessionException) e), self());
            } else {
                log().error("Unable to get BAS response. SubID: " + invokeBASServiceMsg
                        .getSubId());
                getContext().sender().tell(CommonHelper.getActivationResponseFromException(
                        new HHSessionException(config.getString("resultdescriptions.hh.systemerror"),
                                e)), self());
            }
        }
    }

    /**
     * Method that validates BAS Response
     */
    private InvokeIUMActivateMsg getMsgToInvokeIUM(String response, InvokeBASServiceMsg basMessage)
            throws HHSessionException {
        try {
            BASSubscriberResponse basSubscriberResponse = mapper.readValue(response, BASSubscriberResponse.class);
            if (null != basSubscriberResponse
                    && null != basSubscriberResponse.getStatus()
                    && null != basSubscriberResponse.getStatus().getCode()
                    && null != basSubscriberResponse.getContent()) {
                if (config.getString("resultcodes.bas.subscriptions.success").equalsIgnoreCase(
                        basSubscriberResponse.getStatus().getCode())) {
                    String ctn = null;
                    for (BASContent basContent : basSubscriberResponse.getContent()) {
                        if (null != basContent.getId() && null != basContent.getNumber()
                                && basMessage.getSubId().equalsIgnoreCase(basContent.getId())) {
                            ctn = basContent.getNumber();
                        }
                    }
                    if (null != ctn) {
                        return new InvokeIUMActivateMsg(ctn, basMessage.getSubId(), basMessage.getCacheHandler());
                    }
                }
            }
        } catch (IOException e) {
            log().error("Unable to parse BAS response. SubID: " + basMessage
                    .getSubId() + ". IOException: " + e.getMessage());
            throw new HHSessionException(config.getString("resultdescriptions.hh.systemerror"), e);
        }
        log().error("Unable to get CTN for specific id from BAS response. SUBID: " + basMessage
                .getSubId());
        throw new HHSessionException(config.getString("resultcodes.hh.validationfailed"),
                config.getString("resultdescriptions.hh.validationfailed"),
                new Exception("Unable to fetch CTN from BAS response"));
    }

}
