package de.domesoft.levelupapi.request;

import java.net.http.HttpRequest;

public class RequestHandler {
    private RequestHandler requestHandler;
    private RequestHandler(){
        requestHandler = new RequestHandler();
    }
    public String getRequest(){
    return null;
    }
    public RequestHandler getRequestHandler() {
        if(requestHandler == null){
            requestHandler = new RequestHandler();
        }
        return requestHandler;
    }
}
