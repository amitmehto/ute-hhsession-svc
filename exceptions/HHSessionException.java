package com.rogers.ute.hhsessionsvc.exceptions;

public class HHSessionException extends Exception {

    private String hhCode;
    private String hhMessage;
    private Exception exception;

    public HHSessionException(String msg) {
        super();
        this.hhMessage = msg;
    }

    public HHSessionException(String msg, Exception e) {
        super();
        this.hhMessage = msg;
        this.exception = e;
    }

    public HHSessionException(String code, String msg, Exception e) {
        super();
        this.hhCode = code;
        this.hhMessage = msg;
        this.exception = e;
    }

    public String getHhCode() {
        return hhCode;
    }

    public void setHhCode(String hhCode) {
        this.hhCode = hhCode;
    }

    public String getHhMessage() {
        return hhMessage;
    }
    public void setHhMessage(String hhMessage) {
        this.hhMessage = hhMessage;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
