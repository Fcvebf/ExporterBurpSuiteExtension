package gr.fcvebf.burpexporterplugin.utils;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.Http;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.RequestOptions;

import java.net.*;
import java.nio.charset.StandardCharsets;



public class HttpWrapper {

    public static enum HttpMethod {GET, POST, PUT;}


    public static burp.api.montoya.http.message.responses.HttpResponse performRequestWithMontoya(MontoyaApi m_api, String urlString, String jsonPayload, String method, String extraCookie, boolean debug)
    {
        try {

            Http http = m_api.http();

            URI uri = URI.create(urlString);
            String host = uri.getHost();
            int port = uri.getPort() != -1 ? uri.getPort() : (uri.getScheme().equalsIgnoreCase("https") ? 443 : 80);
            boolean useHttps = uri.getScheme().equalsIgnoreCase("https");
            HttpService service = HttpService.httpService(host, port, useHttps);


            // Determine path and query
            String path = uri.getRawPath();
            if (uri.getRawQuery() != null) {
                path += "?" + uri.getRawQuery();
            }

            // Build the request
            StringBuilder rawRequest = new StringBuilder();
            rawRequest.append(method+" "+path+" HTTP/1.1\r\n");

            // Required headers
            rawRequest.append("Host: ").append(service.host()+":"+port).append("\r\n");
            rawRequest.append("Content-Type: application/json;charset=utf-8\r\n");
            rawRequest.append("Accept: application/json, text/plain, */*\r\n");
            if(method!="GET")
            {
                rawRequest.append("Content-Length: ").append(jsonPayload.getBytes(StandardCharsets.UTF_8).length).append("\r\n");
            }
            // Optional cookie
            if (extraCookie != null && !extraCookie.isBlank()) {
                rawRequest.append("Cookie: ").append(extraCookie).append("\r\n");
            }
            rawRequest.append("\r\n"); // End of headers

            if(method!="GET") {
                rawRequest.append(jsonPayload); // Body
            }

            burp.api.montoya.http.message.requests.HttpRequest httprequest = burp.api.montoya.http.message.requests.HttpRequest.httpRequest(service, rawRequest.toString());

            RequestOptions requestOptions = RequestOptions.requestOptions().withResponseTimeout(Config.longTimeoutMillis);
            burp.api.montoya.http.message.HttpRequestResponse reqres = http.sendRequest(httprequest,requestOptions);

            if (debug) {
                m_api.logging().logToOutput("\n------------------------------------------  Request ------------------------------------------");
                if(rawRequest!=null)
                    m_api.logging().logToOutput(rawRequest.toString());
                else
                    m_api.logging().logToOutput("EMPTY REQUEST....");

                m_api.logging().logToOutput("---  Response ---");
                if(reqres !=null && reqres.response()!=null)
                    m_api.logging().logToOutput(reqres.response().toString());
                else
                    m_api.logging().logToOutput("EMPTY RESPONSE....");
            }

            return reqres.response();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }








}
