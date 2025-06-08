package gr.fcvebf.burpexporterplugin.utils;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.Http;
import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.RequestOptions;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;


import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HttpWrapper  implements X509TrustManager {

    public static enum HttpMethod {GET, POST, PUT;}

    public static HttpResponse<String> performRequest(String urlString, String jsonPayload, HttpMethod method, String extraCookie, ProxyConfig proxyConfig, boolean debug
    ) throws Exception {


        install();
        HttpClient.Builder clientBuilder = HttpClient.newBuilder();

        // Configure Proxy if provided
        if (proxyConfig != null) {
            if (proxyConfig.getHost() != null && proxyConfig.getPort() > 0) {
                clientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxyConfig.getHost(), proxyConfig.getPort())));
                if (proxyConfig.getUsername().isPresent() && proxyConfig.getPassword().isPresent()) {
                    clientBuilder.authenticator(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            if (getRequestingHost().equalsIgnoreCase(proxyConfig.getHost()) && getRequestingPort() == proxyConfig.getPort()) {
                                return new PasswordAuthentication(proxyConfig.getUsername().get(), proxyConfig.getPassword().get().toCharArray());
                            }
                            return null;
                        }
                    });
                }
            }
        }


        TrustManager trustAllCerts = new X509ExtendedTrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[0];
            }
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException { }
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException { }
            public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException { }
            public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException { }
            public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException { }
            public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException { }
        };
        TrustManager[] trustManagers=new TrustManager[1];
        trustManagers[0]=trustAllCerts;


        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagers, new SecureRandom());

        // Bypass hostname verification by setting custom SSLParameters
        SSLParameters sslParameters = new SSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm(null);


        HttpClient client = HttpClient.newBuilder().sslContext(sslContext).sslParameters(sslParameters).build();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(urlString)).header("Content-Type", "application/json");

        if (extraCookie != null && !extraCookie.trim().isEmpty()) {
            requestBuilder.header("Cookie", extraCookie);
        }

        switch (method) {
            case GET:
                requestBuilder.GET();
                break;
            case POST:
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8));
                break;
            case PUT:
                requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8));
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        HttpRequest request = requestBuilder.build();

        if (debug) {
            System.out.println("--- Debugging Information ---");
            System.out.println("Request URL: " + urlString);
            System.out.println("HTTP Method: " + method);
            System.out.println("Request Headers: " + request.headers().map());
            System.out.println("Request Body: " + jsonPayload);
            if (proxyConfig != null) {
                System.out.println("Proxy Host: " + proxyConfig.getHost());
                System.out.println("Proxy Port: " + proxyConfig.getPort());
                proxyConfig.getUsername().ifPresent(user -> System.out.println("Proxy User: " + user));
            } else {
                System.out.println("No proxy configured for this request.");
            }
            System.out.println("--- End Debugging Information ---");
        }


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        if (debug) {
            System.out.println("--- Debugging Response ---");
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Headers: " + response.headers().map());
            System.out.println("Response Body: " + response.body());
            System.out.println("--- End Debugging Response ---");
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
        }
        return response;
    }



    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public static void install() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertificates()};
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }



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





    public static CompletableFuture<CustomHttpResponse> performRequestWithMontoyaThreaded(MontoyaApi api, String urlString, String jsonPayload, String method, String extraCookie, boolean debug) {
        return CompletableFuture.supplyAsync(() -> {
            try {

                Http http = api.http();

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
                rawRequest.append(method + " " + path + " HTTP/1.1\r\n");

                // Required headers
                rawRequest.append("Host: ").append(service.host()).append("\r\n");
                rawRequest.append("Content-Type: application/json\r\n");
                rawRequest.append("Content-Length: ").append(jsonPayload.getBytes(StandardCharsets.UTF_8).length).append("\r\n");

                // Optional cookie
                if (extraCookie != null && !extraCookie.isBlank()) {
                    rawRequest.append("Cookie: ").append(extraCookie).append("\r\n");
                }

                rawRequest.append("\r\n"); // End of headers
                rawRequest.append(jsonPayload); // Body

                burp.api.montoya.http.message.requests.HttpRequest httprequest = burp.api.montoya.http.message.requests.HttpRequest.httpRequest(service, rawRequest.toString().getBytes(StandardCharsets.UTF_8).toString());

                if (debug) {
                    System.out.println("--- Debugging Request ---");
                    System.out.println("URL: " + urlString);
                    System.out.println("Method: " + method);
                    //System.out.println("Headers: " + headers);
                    System.out.println("Body: " + jsonPayload);
                }

                burp.api.montoya.http.message.HttpRequestResponse reqres = http.sendRequest(httprequest);

                if (debug) {
                    System.out.println("--- Debugging Response ---");
                    System.out.println("Status Code: " + reqres.response().statusCode());
                    System.out.println("Headers: " + reqres.response().headers());
                    //System.out.println("Body: " + response.bodyToString());
                }


                return new CustomHttpResponse(reqres.response().statusCode(), reqres.response().headers(), reqres.response().bodyToString());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }





}
