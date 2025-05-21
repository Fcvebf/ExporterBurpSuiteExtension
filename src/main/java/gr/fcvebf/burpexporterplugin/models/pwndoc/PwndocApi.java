package gr.fcvebf.burpexporterplugin.models.pwndoc;

import burp.api.montoya.MontoyaApi;
import gr.fcvebf.burpexporterplugin.utils.ProxyConfig;
import gr.fcvebf.burpexporterplugin.utils.HttpWrapper;
import static gr.fcvebf.burpexporterplugin.utils.HttpWrapper.performRequest;
import static gr.fcvebf.burpexporterplugin.utils.HttpWrapper.performRequestWithMontoya;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gr.fcvebf.burpexporterplugin.utils.Utilities;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class PwndocApi {

    public String pwndocURL;
    public String username;
    public String password;
    public String token_cookie;
    public ProxyConfig proxyConfig;
    public boolean debug;
    public boolean useMontoya;
    public MontoyaApi montoyaApi;



    public PwndocApi(String pwndocURL, ProxyConfig proxyConfig, boolean debug,boolean useMontoya,MontoyaApi montoyaApi)
    {
        String temppwndocURL=pwndocURL;
        if(pwndocURL.endsWith("/"))
            temppwndocURL=pwndocURL.substring(0, pwndocURL.length() - 1);
        this.pwndocURL = temppwndocURL;

        this.token_cookie = "";
        this.proxyConfig = proxyConfig;
        this.debug = debug;
        this.useMontoya=useMontoya;
        this.montoyaApi=montoyaApi;
    }




    public CompletableFuture<Boolean> LoginAsync(String username,String password) throws Exception
    {
        return CompletableFuture.supplyAsync(() -> {
            try {
                this.username = username;
                this.password = password;

                Pwndoc p = new Pwndoc(this.pwndocURL + Endpoints.login, this.username, this.password, this);
                this.token_cookie = p.token_cookie.replace(": ", "=");
                if (p.token_cookie != "") {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        });
    }


    public boolean Login(String username,String password) throws Exception
    {

        this.username = username;
        this.password = password;

        Pwndoc p=new Pwndoc(this.pwndocURL+Endpoints.login,this.username,this.password,this);
        this.token_cookie = p.token_cookie.replace(": ","=");
        if (p.token_cookie!="") {
            return true;
        }
        else
        {
            return false;
        }
    }



    public CompletableFuture<List<AuditType>> AuditTypesGetAsync() throws Exception
    {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<AuditType> auditTypes = null;
                HttpResponse response = performRequest(this.pwndocURL + Endpoints.audit_types, null, HttpWrapper.HttpMethod.GET, this.token_cookie, this.proxyConfig, this.debug);
                ApiResponse<?> apiResponse = ApiParser.parseResponse(Endpoints.audit_types, response);

                if (apiResponse.getDatas() instanceof List) {
                    List<?> datas = (List<?>) apiResponse.getDatas();
                    if (!datas.isEmpty() && datas.get(0) instanceof AuditType) {
                        auditTypes = (List<AuditType>) datas;
                        auditTypes.forEach(a -> System.out.println(a.getName()));
                    }
                }
                return auditTypes;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    public List<AuditType> AuditTypesGet() throws Exception
    {
        List<AuditType> auditTypes = null;
        try {

            ApiResponse<?> apiResponse;
            String endpoint = this.pwndocURL + Endpoints.audit_types;

            if (!this.useMontoya) {
                HttpResponse response = performRequest(endpoint, null, HttpWrapper.HttpMethod.GET, this.token_cookie, this.proxyConfig, this.debug);
                apiResponse = ApiParser.parseResponse(Endpoints.audit_types, response);
            }
            else {
                burp.api.montoya.http.message.responses.HttpResponse response = performRequestWithMontoya(this.montoyaApi, endpoint, null, "GET", this.token_cookie, this.debug);
                apiResponse = ApiParser.parseResponse(Endpoints.audit_types, response.bodyToString());
            }

            if (apiResponse.getDatas() instanceof List) {
                List<?> datas = (List<?>) apiResponse.getDatas();
                if (!datas.isEmpty() && datas.get(0) instanceof AuditType) {
                    auditTypes = (List<AuditType>) datas;
                    auditTypes.forEach(a -> System.out.println(a.getName()));
                }
            }

        } catch (Exception e) {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
        }
        return auditTypes;
    }




    public CompletableFuture<List<VulnType>> VulnTypesGetAsync() throws Exception {
        return CompletableFuture.supplyAsync(() -> {
            List<VulnType> vulnTypes = null;
            HttpResponse response = null;
            try {
                response = performRequest(this.pwndocURL + Endpoints.vulntypes, null, HttpWrapper.HttpMethod.GET, this.token_cookie, this.proxyConfig, this.debug);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ApiResponse<?> apiResponse = null;
            try {
                apiResponse = ApiParser.parseResponse(Endpoints.vulntypes, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (apiResponse.getDatas() instanceof List) {
                List<?> datas = (List<?>) apiResponse.getDatas();
                if (!datas.isEmpty() && datas.get(0) instanceof VulnType) {
                    vulnTypes = (List<VulnType>) datas;
                    vulnTypes.forEach(a -> System.out.println(a.getName()));
                }
            }
            return vulnTypes;
        });

    }


    public List<VulnType> VulnTypesGet() throws Exception
    {
        List<VulnType> vulnTypes = null;
        try {

            ApiResponse<?> apiResponse;
            String endpoint = this.pwndocURL + Endpoints.vulntypes;

            if (!this.useMontoya) {
                HttpResponse response = performRequest(endpoint, null, HttpWrapper.HttpMethod.GET, this.token_cookie, this.proxyConfig, this.debug);
                apiResponse = ApiParser.parseResponse(Endpoints.vulntypes, response);
            } else {
                burp.api.montoya.http.message.responses.HttpResponse response = performRequestWithMontoya(this.montoyaApi, endpoint, null, "GET", this.token_cookie, this.debug);
                apiResponse = ApiParser.parseResponse(Endpoints.vulntypes, response.bodyToString());
            }


            if (apiResponse.getDatas() instanceof List) {
                List<?> datas = (List<?>) apiResponse.getDatas();
                if (!datas.isEmpty() && datas.get(0) instanceof VulnType) {
                    vulnTypes = (List<VulnType>) datas;
                    vulnTypes.forEach(a -> System.out.println(a.getName()));
                }
            }

        } catch (Exception e) {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
        }
        return vulnTypes;
    }




    public CompletableFuture<List<VulnCategory>> VulnCategoriesGetAsync() throws Exception
    {
        return CompletableFuture.supplyAsync(() -> {
            List<VulnCategory> vulnCategories = null;
            HttpResponse response = null;
            try {
                response = performRequest(this.pwndocURL + Endpoints.vulncategories, null, HttpWrapper.HttpMethod.GET, this.token_cookie, this.proxyConfig, this.debug);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ApiResponse<?> apiResponse = null;
            try {
                apiResponse = ApiParser.parseResponse(Endpoints.vulncategories, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (apiResponse.getDatas() instanceof List) {
                List<?> datas = (List<?>) apiResponse.getDatas();
                if (!datas.isEmpty() && datas.get(0) instanceof VulnCategory) {
                    vulnCategories = (List<VulnCategory>) datas;
                    vulnCategories.forEach(a -> System.out.println(a.getName()));
                }
            }
            return vulnCategories;
        });
    }


    public List<VulnCategory> VulnCategoriesGet() throws Exception
    {
        List<VulnCategory> vulnCategories = null;
        try {
            ApiResponse<?> apiResponse;
            String endpoint = this.pwndocURL + Endpoints.vulncategories;

            if (!this.useMontoya) {
                HttpResponse response = performRequest(endpoint, null, HttpWrapper.HttpMethod.GET, this.token_cookie, this.proxyConfig, this.debug);
                apiResponse = ApiParser.parseResponse(Endpoints.vulncategories, response);
            } else {
                burp.api.montoya.http.message.responses.HttpResponse response = performRequestWithMontoya(this.montoyaApi, endpoint, null, "GET", this.token_cookie, this.debug);
                apiResponse = ApiParser.parseResponse(Endpoints.vulncategories, response.bodyToString());
            }

            if (apiResponse.getDatas() instanceof List) {
                List<?> datas = (List<?>) apiResponse.getDatas();
                if (!datas.isEmpty() && datas.get(0) instanceof VulnCategory) {
                    vulnCategories = (List<VulnCategory>) datas;
                    vulnCategories.forEach(a -> System.out.println(a.getName()));
                }
            }

        } catch (Exception e) {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
        }
        return vulnCategories;
    }




    public CompletableFuture<List<Language>> LanguagesGetAsync() throws Exception
    {
        return CompletableFuture.supplyAsync(() -> {
            List<Language> languages = null;
            HttpResponse response = null;
            try {
                response = performRequest(this.pwndocURL + Endpoints.languages, null, HttpWrapper.HttpMethod.GET, this.token_cookie, this.proxyConfig, this.debug);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ApiResponse<?> apiResponse = null;
            try {
                apiResponse = ApiParser.parseResponse(Endpoints.languages, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (apiResponse.getDatas() instanceof List) {
                List<?> datas = (List<?>) apiResponse.getDatas();
                if (!datas.isEmpty() && datas.get(0) instanceof Language) {
                    languages = (List<Language>) datas;
                    languages.forEach(a -> System.out.println(a.getLanguage()));
                }
            }
            return languages;
        });
    }



    public List<Language> LanguagesGet() throws Exception
    {
        List<Language> languages = null;
        try {

            ApiResponse<?> apiResponse;
            String endpoint = this.pwndocURL + Endpoints.languages;

            if (!this.useMontoya) {
                HttpResponse response = performRequest(endpoint, null, HttpWrapper.HttpMethod.GET, this.token_cookie, this.proxyConfig, this.debug);
                apiResponse = ApiParser.parseResponse(Endpoints.languages, response);
            } else {
                burp.api.montoya.http.message.responses.HttpResponse response = performRequestWithMontoya(this.montoyaApi, endpoint, null, "GET", this.token_cookie, this.debug);
                apiResponse = ApiParser.parseResponse(Endpoints.languages, response.bodyToString());
            }

            if (apiResponse.getDatas() instanceof List) {
                List<?> datas = (List<?>) apiResponse.getDatas();
                if (!datas.isEmpty() && datas.get(0) instanceof Language) {
                    languages = (List<Language>) datas;
                    languages.forEach(a -> System.out.println(a.getLanguage()));
                }
            }

        } catch (Exception e) {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
        }
        return languages;
    }




    public CompletableFuture<List<String[]>> AuditsGetAllNamesAsync() throws Exception
    {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse response = performRequest(this.pwndocURL + Endpoints.audits, null, HttpWrapper.HttpMethod.GET, this.token_cookie, this.proxyConfig, this.debug);
                JsonNode datas = getDatasAttribute(response.body().toString());
                List<String[]> idAndNames = new ArrayList<>();

                if (datas != null && datas.isArray()) {
                    for (JsonNode dataObject : datas) {
                        JsonNode idNode = dataObject.get("_id");
                        JsonNode nameNode = dataObject.get("name");

                        if (idNode != null && idNode.isTextual() && nameNode != null && nameNode.isTextual()) {
                            idAndNames.add(new String[]{idNode.asText(), nameNode.asText()});
                        }
                    }
                }
                return idAndNames;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }


    public List<String[]> AuditsGetAllNames() throws Exception
    {
        try {
            String endpoint=this.pwndocURL + Endpoints.audits;
            JsonNode datas;

            if(!this.useMontoya)
            {
                HttpResponse response = performRequest(endpoint, null, HttpWrapper.HttpMethod.GET, this.token_cookie, this.proxyConfig, this.debug);
                datas = getDatasAttribute(response.body().toString());
            }
            else
            {
                burp.api.montoya.http.message.responses.HttpResponse response = performRequestWithMontoya(this.montoyaApi, endpoint, null, "GET", this.token_cookie, this.debug);
                datas = getDatasAttribute(response.body().toString());
            }

            List<String[]> idAndNames = new ArrayList<>();

            if (datas != null && datas.isArray()) {
                for (JsonNode dataObject : datas) {
                    JsonNode idNode = dataObject.get("_id");
                    JsonNode nameNode = dataObject.get("name");

                    if (idNode != null && idNode.isTextual() && nameNode != null && nameNode.isTextual()) {
                        idAndNames.add(new String[]{idNode.asText(), nameNode.asText()});
                    }
                }
            }
            return idAndNames;
        } catch (IOException e) {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
        } catch (Exception e) {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
        }
        return  null;
    }




    public CompletableFuture<String> AuditsGetAuditIdByNameAsync(String auditname) throws Exception
    {
        return CompletableFuture.supplyAsync(() -> {
            String auditId = null;
            try {

                List<String[]> audits = AuditsGetAllNames();
                if (audits == null || audits.isEmpty()) {
                    return null;
                } else {
                    for (String[] auditInfo : audits) {
                        if (auditInfo.length >= 2 && auditname.equals(auditInfo[1])) {
                            //Retrieve the Audit By Id
                            return auditInfo[0]; // Found a matching audit name
                        }
                    }
                }
                return auditId;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }


    public String AuditsGetAuditIdByName(String auditname) throws Exception
    {
        String auditId=null;
        try {

            List<String[]> audits = AuditsGetAllNames();
            if (audits == null || audits.isEmpty())
            {
                return  null;
            }
            else
            {
                for (String[] auditInfo : audits)
                {
                    if (auditInfo.length >= 2 && auditname.equals(auditInfo[1]))
                    {
                        //Retrieve the Audit By Id
                        return auditInfo[0]; // Found a matching audit name
                    }
                }
            }
            return auditId;
        } catch (IOException e) {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
        } catch (Exception e) {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
        }
        return  null;

    }






    public CompletableFuture<String> AuditsCreateNewAuditAsync(String pwndocAuditName,String pwndocAuditType, String pwndocLanguage,String type) throws Exception
    {
        return CompletableFuture.supplyAsync(() -> {
            String audit_id = "";
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonObject = objectMapper.createObjectNode();

                jsonObject.put("name", pwndocAuditName);
                jsonObject.put("language", pwndocLanguage);
                jsonObject.put("auditType", pwndocAuditType);
                jsonObject.put("type", type);
                String jsonString = objectMapper.writeValueAsString(jsonObject);


                HttpResponse response = performRequest(this.pwndocURL + Endpoints.audits, jsonString, HttpWrapper.HttpMethod.POST, this.token_cookie, this.proxyConfig, this.debug);

                JsonNode datas = getDatasAttribute(response.body().toString());
                if (datas != null && datas.isObject()) {
                    JsonNode auditNode = datas.get("audit");
                    if (auditNode != null && auditNode.isObject()) {
                        JsonNode idNode = auditNode.get("_id");
                        if (idNode != null && idNode.isTextual()) {
                            audit_id = idNode.asText();
                        } else {
                            throw new Exception("'_id' not found or is not a text value within the 'audit' object.");
                        }
                    } else {
                        throw new Exception("'audit' object not found within 'datas'.");
                    }
                } else {
                    throw new Exception("'datas' is null or not a JSON object.");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {

            }
            return audit_id;
        });
    }



    public String AuditsCreateNewAudit(String pwndocAuditName,String pwndocAuditType, String pwndocLanguage,String type) throws Exception
    {
        String audit_id = "";
        try {
            String endpoint=this.pwndocURL + Endpoints.audits;
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonObject = objectMapper.createObjectNode();

            jsonObject.put("name", pwndocAuditName);
            jsonObject.put("language", pwndocLanguage);
            jsonObject.put("auditType", pwndocAuditType);
            jsonObject.put("type", type);
            String jsonString = objectMapper.writeValueAsString(jsonObject);
            JsonNode datas;

            if(!this.useMontoya)
            {
                HttpResponse response = performRequest(endpoint, jsonString, HttpWrapper.HttpMethod.POST, this.token_cookie, this.proxyConfig, this.debug);
                datas = getDatasAttribute(response.body().toString());
            }
            else
            {
                burp.api.montoya.http.message.responses.HttpResponse response = performRequestWithMontoya(this.montoyaApi, endpoint, jsonString, "POST", this.token_cookie, this.debug);
                datas = getDatasAttribute(response.body().toString());
            }

            if (datas != null && datas.isObject())
            {
                JsonNode auditNode = datas.get("audit");
                if (auditNode != null && auditNode.isObject())
                {
                    JsonNode idNode = auditNode.get("_id");
                    if (idNode != null && idNode.isTextual())
                    {
                        audit_id= idNode.asText();
                    } else
                    {
                        throw new Exception("'_id' not found or is not a text value within the 'audit' object.");
                    }
                }
                else
                {
                    throw new Exception("'audit' object not found within 'datas'.");
                }
            }
            else
            {
                throw new Exception("'datas' is null or not a JSON object.");
            }

        }
        catch (IOException e) {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
        }
        catch (Exception ex)
        {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(ex));
        }
        return audit_id;
    }




    public CompletableFuture<String> AuditsCreateFindingAsync(String auditId,String Findingtitle,String category,String vulnType,String description,String observation,String remediation,String poc,String scope,int remediationComplexity,int priority,String cvssv3) throws Exception
    {
        return CompletableFuture.supplyAsync(() -> {
            String result = "";
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonObject = objectMapper.createObjectNode();

                jsonObject.put("title", Findingtitle);
                jsonObject.put("vulnType", vulnType);
                jsonObject.put("description", description);
                jsonObject.put("observation", observation);
                jsonObject.put("remediation", remediation);
                jsonObject.put("poc", poc);
                jsonObject.put("scope", scope);
                jsonObject.put("remediationComplexity", remediationComplexity);
                jsonObject.put("priority", priority);
                jsonObject.put("cvssv3", cvssv3);
                jsonObject.put("category", category);

                String jsonString = objectMapper.writeValueAsString(jsonObject);

                HttpResponse response = performRequest(this.pwndocURL + Endpoints.audits + "/" + auditId + "/findings", jsonString, HttpWrapper.HttpMethod.POST, this.token_cookie, this.proxyConfig, this.debug);

                JsonNode datas = getStatusAttribute(response.body().toString());
                result = datas.asText();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {

            }
            return result;
        });
    }


    public String AuditsCreateFinding(String auditId,String Findingtitle,String category,String vulnType,String description,String observation,String remediation,String poc,String scope,int remediationComplexity,int priority,String cvssv3) throws Exception
    {
        String result = "";
        try {
            String endpoint=this.pwndocURL + Endpoints.audits + "/" + auditId + "/findings";
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonObject = objectMapper.createObjectNode();

            jsonObject.put("title", Findingtitle);
            jsonObject.put("vulnType", vulnType);
            jsonObject.put("description", description);
            jsonObject.put("observation", observation);
            jsonObject.put("remediation", remediation);
            jsonObject.put("poc", poc);
            jsonObject.put("scope", scope);
            jsonObject.put("remediationComplexity", remediationComplexity);
            jsonObject.put("priority", priority);
            jsonObject.put("cvssv3", cvssv3);
            jsonObject.put("category", category);

            String jsonString = objectMapper.writeValueAsString(jsonObject);
            JsonNode datas;

            if(!this.useMontoya)
            {
                HttpResponse response = performRequest(endpoint, jsonString, HttpWrapper.HttpMethod.POST, this.token_cookie, this.proxyConfig, this.debug);
                datas = getStatusAttribute(response.body().toString());
            }
            else
            {
                burp.api.montoya.http.message.responses.HttpResponse response = performRequestWithMontoya(this.montoyaApi, endpoint, jsonString, "POST", this.token_cookie, this.debug);
                datas = getDatasAttribute(response.body().toString());
            }

            if (datas!=null)
                result=datas.asText();
        }
        catch (IOException e) {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(e));
        }
        catch (Exception ex)
        {
            this.montoyaApi.logging().logToError(Utilities.getStackTraceAsString(ex));
        }
        return result;
    }



    public static JsonNode getStatusAttribute(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode datasNode = rootNode.get("status");
        return datasNode;
    }

    public static JsonNode getDatasAttribute(String jsonResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode datasNode = rootNode.get("datas");
        return datasNode;
    }







}
