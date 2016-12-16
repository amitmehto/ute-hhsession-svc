package com.rogers.ute.hhsessionsvc.helpers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rogers.ute.hhsessionsvc.models.camp.CampStatus;
import com.rogers.ute.hhsessionsvc.models.hhsessionresponses.SessionResponse;
import com.rogers.ute.hhsessionsvc.models.ium.IUMSessionResponse;
import com.typesafe.config.Config;
import com.rogers.ute.hhsessionsvc.exceptions.HHSessionException;
import com.rogers.ute.hhsessionsvc.models.camp.CAMPErrorResponse;
import com.rogers.ute.hhsessionsvc.models.ium.IUMActivationResponse;
import com.rogers.ute.hhsessionsvc.models.hhsessionresponses.ActivationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class IUMHelper {

    private static Logger logger = LoggerFactory.getLogger(IUMHelper.class);

    public enum IUMTransactionType {
        HHActivate, HHActiveSession
    }

    public enum IUMResponseType {
        CAMPError(CAMPErrorResponse.class), HHActivate(IUMActivationResponse.class),
            HHActiveSession(IUMSessionResponse.class);
        private Class classToRefer;
        private IUMResponseType(Class cn) {
            classToRefer = cn;
        }
        public Class getClassToRefer() {
            return classToRefer;
        }
    }

    /**
     * Method that returns the Response object based on IUM Response
     * @param transactionType
     * @param responseFromIUM
     * @param sessionStartTime
     * @param mapper
     * @param config
     * @return
     * @throws HHSessionException
     */
    public static Object processIUMResponse(IUMTransactionType transactionType,
                                            String responseFromIUM, Date sessionStartTime,
                                            ObjectMapper mapper, Config config)
            throws HHSessionException {
        IUMResponseType responseType = getResponseType(config, mapper, responseFromIUM);
        switch (responseType) {
            case CAMPError:
                CAMPErrorResponse campErrorResponse = (CAMPErrorResponse) getObjectFromJsonResponse(responseType,
                        responseFromIUM, mapper, config);
                logger.error(new StringBuilder("CAMP ERROR: ").append(campErrorResponse
                        .getCamp().getStatus().getMessage()).toString());
                throw getHHExceptionFromCAMPCode(config, campErrorResponse.getCamp().getStatus());
            case HHActivate:
                IUMActivationResponse iumActivationResponse = (IUMActivationResponse) getObjectFromJsonResponse(
                        responseType, responseFromIUM, mapper, config);
                switch (transactionType) {
                    case HHActiveSession:
                        return createSessionResponse(iumActivationResponse,
                                sessionStartTime, config);
                    case HHActivate:
                        return createActivationResponse(iumActivationResponse, config);
                    default:
                        logger.error("Transaction type not found");
                        throw new HHSessionException(config
                                .getString("resultdescriptions.hh.systemerror"));
                }
            case HHActiveSession:
                IUMSessionResponse iumSessionResponse = (IUMSessionResponse) getObjectFromJsonResponse(
                        responseType, responseFromIUM, mapper, config);
                switch (transactionType) {
                    case HHActiveSession:
                        return createSessionResponse(iumSessionResponse,
                                sessionStartTime, config);
                    case HHActivate:
                        return createActivationResponse(iumSessionResponse, config);
                    default:
                        logger.error("Transaction type not found");
                        throw new HHSessionException(config
                                .getString("resultdescriptions.hh.systemerror"));
                }
            default:
                logger.error("Response type not found");
                throw new HHSessionException(config
                        .getString("resultdescriptions.hh.systemerror"));
        }
    }

    /**
     * Method that identifies the response type
     * @param config
     * @param mapper
     * @param responseFromIUM
     * @return
     * @throws HHSessionException
     */
    private static IUMResponseType getResponseType(Config config, ObjectMapper mapper, String responseFromIUM)
            throws HHSessionException {
        try {
            JsonNode jsonFromResponse = mapper.readTree(responseFromIUM);
            if (jsonFromResponse.has("CAMP")) {
                return IUMResponseType.CAMPError;
            } else if (jsonFromResponse.has("activateHHResponse")) {
                return IUMResponseType.HHActivate;
            } else if (jsonFromResponse.has("getHHUsageResponse")) {
                return IUMResponseType.HHActiveSession;
            } else {
                logger.error("Unable to identify response");
                throw new HHSessionException(config.getString("resultdescriptions.hh.systemerror"));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new HHSessionException(config.getString("resultdescriptions.hh.systemerror"), e);
        }
    }


    /**
     * Method that generates final Activation Response based on IUM activation response object
     * @param obj
     * @return
     */
    private static ActivationResponse createActivationResponse(IUMActivationResponse obj, Config config) {
        ActivationResponse response = new ActivationResponse();
        if (null != obj.getActivateHHResponse()
                && null != obj.getActivateHHResponse().getResultCode()
                && null != obj.getActivateHHResponse().getResultDescription()) {
            // Check for status code and description
            HashMap<String, String> mappedCodes = getMappedCodesFromIUMResponse(config, obj.getActivateHHResponse().getResultCode(),
                    obj.getActivateHHResponse().getResultDescription(), IUMResponseType.HHActivate);
            response.setResultCode(mappedCodes.get("code"));
            response.setResultDesc(mappedCodes.get("description"));
            // Setting time
            response.setStartTime(CommonHelper.convertUnixTime(obj.getActivateHHResponse()
                    .getHhSessionStartTime()));
            // Setting remaining response properties
            response.setDataBucketSize(obj.getActivateHHResponse().getHhSessionBucketSize());
            response.setHhSessionId(obj.getActivateHHResponse().getHhSessionId());
            response.setMaxTime(obj.getActivateHHResponse().getHhSessionDuration());

        } else {
            response.setResultCode(config.getString("resultcodes.hh.validationfailed"));
            response.setResultDesc(config.getString("resultdescriptions.hh.validationfailed"));
        }
        return response;
    }

    /**
     * Method that generates final Activation Response based on IUM session response object
     * @param obj
     * @return
     */
    private static ActivationResponse createActivationResponse(IUMSessionResponse obj, Config config) {
        ActivationResponse response = new ActivationResponse();
        if (null != obj.getGetHHUsageResponse()
                && null != obj.getGetHHUsageResponse().getResultCode()
                && null != obj.getGetHHUsageResponse().getResultDescription()) {
            // Check for status code and description
            HashMap<String, String> mappedCodes = getMappedCodesFromIUMResponse(config, obj.getGetHHUsageResponse()
                            .getResultCode(), obj.getGetHHUsageResponse().getResultDescription(),
                    IUMResponseType.HHActiveSession);
            response.setResultCode(mappedCodes.get("code"));
            response.setResultDesc(mappedCodes.get("description"));
            // Setting time
            response.setStartTime(CommonHelper.convertUnixTime(obj.getGetHHUsageResponse()
                    .getHhSessionStartTime()));
            // Setting remaining response properties
            response.setDataBucketSize(obj.getGetHHUsageResponse().getHhSessionBucketSize());
            response.setHhSessionId(obj.getGetHHUsageResponse().getHhSessionId());
            response.setMaxTime(obj.getGetHHUsageResponse().getHhSessionDuration());

        } else {
            response.setResultCode(config.getString("resultcodes.hh.validationfailed"));
            response.setResultDesc(config.getString("resultdescriptions.hh.validationfailed"));
        }
        return response;
    }

    /**
     * Method that generates final Activation Response based on IUM session response object
     * @param obj
     * @return
     */
    private static SessionResponse createSessionResponse(IUMSessionResponse obj, Date sessionStartTime, Config config) {
        SessionResponse response = new SessionResponse();
        if (null != obj.getGetHHUsageResponse()
                && null != obj.getGetHHUsageResponse().getResultCode()) {
            // Check for status code and description
            HashMap<String, String> mappedCodes = getMappedCodesFromIUMResponse(config, obj.getGetHHUsageResponse()
                            .getResultCode(), obj.getGetHHUsageResponse().getResultDescription(),
                    IUMResponseType.HHActiveSession);
            response.setResultCode(mappedCodes.get("code"));
            // Setting time
            response.setStartTime(CommonHelper.convertUnixTime(obj.getGetHHUsageResponse()
                    .getHhSessionStartTime()));
            //Setting elapsed time in seconds
            if (null != sessionStartTime) {
                response.setElapsedTime(String.valueOf((System.currentTimeMillis() - sessionStartTime.getTime())/1000));
                response.setLastModifiedTime(CommonHelper.convertUnixTime(String.valueOf(System.currentTimeMillis())));
            }
            // Setting remaining response properties
            response.setDataBucketSize(obj.getGetHHUsageResponse().getHhSessionBucketSize());
            response.setHhSessionId(obj.getGetHHUsageResponse().getHhSessionId());
            response.setMaxTime(obj.getGetHHUsageResponse().getHhSessionDuration());
            response.setDataConsumed(obj.getGetHHUsageResponse().getHhConsumedUsage());
        } else {
            response.setResultCode(config.getString("resultcodes.hh.validationfailed"));
        }
        return response;
    }

    /**
     * Method that generates final Activation Response based on IUM activation response object
     * @param obj
     * @return
     */
    private static SessionResponse createSessionResponse(IUMActivationResponse obj, Date sessionStartTime, Config config) {
        SessionResponse response = new SessionResponse();
        if (null != obj.getActivateHHResponse()
                && null != obj.getActivateHHResponse().getResultCode()) {
            // Check for status code and description
            HashMap<String, String> mappedCodes = getMappedCodesFromIUMResponse(config, obj.getActivateHHResponse()
                            .getResultCode(),obj.getActivateHHResponse().getResultDescription(),
                    IUMResponseType.HHActivate);
            response.setResultCode(mappedCodes.get("code"));
            // Setting time
            response.setStartTime(CommonHelper.convertUnixTime(obj.getActivateHHResponse()
                    .getHhSessionStartTime()));
            //Setting elapsed time in seconds
            if (null != sessionStartTime) {
                response.setElapsedTime(String.valueOf((System.currentTimeMillis() - sessionStartTime.getTime())/1000));
                response.setLastModifiedTime(CommonHelper.convertUnixTime(String.valueOf(System.currentTimeMillis())));
            }
            // Setting remaining response properties
            response.setDataBucketSize(obj.getActivateHHResponse().getHhSessionBucketSize());
            response.setHhSessionId(obj.getActivateHHResponse().getHhSessionId());
            response.setMaxTime(obj.getActivateHHResponse().getHhSessionDuration());
        } else {
            response.setResultCode(config.getString("resultcodes.hh.validationfailed"));
        }
        return response;
    }


    /**
     * Method that converts Json String to POJO
     * @param responseType
     * @param json
     * @param mapper
     * @return
     * @throws HHSessionException
     */
    private static Object getObjectFromJsonResponse(IUMResponseType responseType, String json,
                                                    ObjectMapper mapper, Config config) throws HHSessionException {
        Object response;
        try {
            response = mapper.readValue(json, responseType.getClassToRefer());
        } catch (IOException e) {
            e.printStackTrace();
            throw new HHSessionException(config.getString("resultcodes.hh.validationfailed"),
                    config.getString("resultdescriptions.hh.validationfailed"), e);
        }
        return response;
    }


    /**
     * Method that maps camp error codes to HHSessionException
     * @param config
     * @param status
     * @return
     */
    private static HHSessionException getHHExceptionFromCAMPCode(Config config, CampStatus status) {
        List<String> codes_validationfailed = config.getStringList("resultcodes.camp.validationfailed");
        List<String> codes_connectiontimeout = config.getStringList("resultcodes.camp.connectiontimeout");
        for (String code : codes_connectiontimeout) {
            if (code.equalsIgnoreCase(status.getCode())){
                return new HHSessionException(config.getString("resultcodes.hh.connectiontimeout"),
                        config.getString("resultdescriptions.hh.connectiontimeout"), new Exception());
            }
        }
        for (String code : codes_validationfailed) {
            if (code.equalsIgnoreCase(status.getCode())){
                return new HHSessionException(config.getString("resultcodes.hh.validationfailed"),
                        config.getString("resultdescriptions.hh.validationfailed"), new Exception());
            }
        }
        return new HHSessionException(config.getString("resultcodes.hh.systemerror"),
                config.getString("resultdescriptions.hh.systemerror"), new Exception());
    }

    /**
     * Method that maps ium codes
     * @param iumCode
     * @param iumDescription
     * @param responseType
     * @return
     */
    private static HashMap<String, String> getMappedCodesFromIUMResponse(Config config, String iumCode, String iumDescription,
                                                                         IUMResponseType responseType) {
        HashMap<String, String> responseMap = new HashMap<>();
        boolean isMapped = false;
        String mappedCode = iumCode;
        String mappedDescription = iumDescription;
        List<String> codes_success = (responseType == IUMResponseType.HHActivate) ?
                config.getStringList("resultcodes.ium.activatehh.success")
                : config.getStringList("resultcodes.ium.gethhusage.success");
        for (String code : codes_success) {
            if (code.equalsIgnoreCase(iumCode)){
                mappedCode = config.getString("resultcodes.hh.success");
                mappedDescription = config.getString("resultdescriptions.hh.success");
                isMapped = true;
            }
        }
        if (!isMapped) {
            List<String> codes_connectionTimeout = config.getStringList("resultcodes.ium.connectiontimeout");
            for (String code : codes_connectionTimeout) {
                if (code.equalsIgnoreCase(iumCode)){
                    mappedCode = config.getString("resultcodes.hh.connectiontimeout");
                    mappedDescription = config.getString("resultdescriptions.hh.connectiontimeout");
                    isMapped = true;
                }
            }
        }
        if (!isMapped) {
            List<String> codes_validationFailed = config.getStringList("resultcodes.ium.validationfailed");
            for (String code : codes_validationFailed) {
                if (code.equalsIgnoreCase(iumCode)){
                    mappedCode = responseMap.put("code", config.getString("resultcodes.hh.validationfailed"));
                    mappedDescription = responseMap.put("description", config.getString("resultdescriptions.hh.validationfailed"));
                }
            }
        }
        responseMap.put("code", mappedCode);
        responseMap.put("description", mappedDescription);
        return responseMap;
    }


    /**
     * Method that creates a Trx Id based on sub id and unix timestamp
     * @param subId
     * @return
     */
    public static String getTrxId(String subId) {
        String subIdWithoutHyphens = subId.replaceAll("-", "");
        long currentUnixTime = System.currentTimeMillis();
        return subIdWithoutHyphens.concat(String.valueOf(currentUnixTime));
    }
}
