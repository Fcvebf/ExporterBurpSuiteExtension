package gr.fcvebf.burpexporterplugin.utils;

import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.HttpHeader;
import java.util.List;

// A custom class to represent the HTTP response with the body as a String
public class CustomHttpResponse {
    private final short statusCode;
    private final List<HttpHeader> headers;
    private final String body;
    //private final HttpService service; // The service the request was sent to

    public CustomHttpResponse(short statusCode, List<HttpHeader> headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
        //this.service = service;
    }

    public int statusCode() {
        return statusCode;
    }

    public List<HttpHeader> headers() {
        return headers;
    }

    public String body() {
        return body;
    }



    @Override
    public String toString() {
        return "CustomHttpResponse{" +
                "statusCode=" + statusCode +
                ", bodyLength=" + (body != null ? body.length() : 0) +
                '}';
    }
}