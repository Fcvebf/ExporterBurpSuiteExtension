package gr.fcvebf.burpexporterplugin.models.pwndoc;

import burp.api.montoya.MontoyaApi;
import gr.fcvebf.burpexporterplugin.utils.HttpWrapper;
import static gr.fcvebf.burpexporterplugin.utils.HttpWrapper.performRequest;
import static gr.fcvebf.burpexporterplugin.utils.HttpWrapper.performRequestWithMontoya;

import java.net.http.HttpResponse;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Pwndoc {

    public String pwndocURL;
    public String username;
    public String password;
    public String token_cookie;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Pwndoc(String pwndocURL, String username, String password, PwndocApi papi) throws Exception {
        this.pwndocURL = pwndocURL;
        this.username = username;
        this.password = password;

        LoginObject postData=new LoginObject(this.username,this.password,"");
        String jsonPayload = objectMapper.writeValueAsString(postData);
        String tokenValue = null;

        if(!papi.useMontoya)
        {
            HttpResponse response = performRequest(pwndocURL, jsonPayload, HttpWrapper.HttpMethod.POST, null, papi.proxyConfig, papi.debug);
            List<String> setCookieHeaders = response.headers().allValues("Set-Cookie");

            for (String cookieHeader : setCookieHeaders) {
                if (cookieHeader.startsWith("token=")) {
                    String tokenWithValueAndAttributes = cookieHeader.substring("token=".length());
                    tokenValue = tokenWithValueAndAttributes.split(";")[0];
                    break; // Found the token, no need to check further
                }
            }
        }
        else {
            burp.api.montoya.http.message.responses.HttpResponse response = performRequestWithMontoya(papi.montoyaApi, pwndocURL, jsonPayload, "POST", null, papi.debug);
            List<burp.api.montoya.http.message.HttpHeader> setCookieHeaders = response.headers();
            for (burp.api.montoya.http.message.HttpHeader cookieHeader : setCookieHeaders) {
                if (cookieHeader.toString().contains("token=")) {
                    String tokenWithValueAndAttributes = cookieHeader.value().substring("token=".length());
                    tokenValue = tokenWithValueAndAttributes.split(";")[0];
                    break; // Found the token, no need to check further
                }
            }

        }
        if (tokenValue != null)
        {
            this.token_cookie = "token: " + tokenValue;
        }
        else
        {
            this.token_cookie="";
        }

    }


    public class LoginObject
    {
        public String username;
        public String password;
        public String totpToken;

        public LoginObject(String username, String password, String totpToken) {
            this.username = username;
            this.password = password;
            this.totpToken = totpToken;
        }
    }

}
