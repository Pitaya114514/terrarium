package com.pitaya.terrarium.client.render.resources;

public class ResourceLoadingException extends Exception {
    public ResourceLoadingException() {
    }

    public ResourceLoadingException(String message) {
        super(message);
    }

    public ResourceLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceLoadingException(Throwable cause) {
        super(cause);
    }

    public ResourceLoadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
