package gr.fcvebf.burpexporterplugin.models.ollama;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.ollama.OllamaChatModel;
import gr.fcvebf.burpexporterplugin.utils.Config;
import gr.fcvebf.burpexporterplugin.utils.Constants;


import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gr.fcvebf.burpexporterplugin.utils.HttpWrapper.performRequestWithMontoya;

public class OllamaClient {

    public String endpoint;
    public MontoyaApi montoyaApi;
    public boolean debug;

    public OllamaClient(String endpoint,MontoyaApi montoyaApi,boolean debug)
    {
        this.endpoint=endpoint;
        this.montoyaApi=montoyaApi;
        this.debug=debug;
    }


    public List<OllamaModel> GetOllamaModels() throws Exception {
        List<OllamaModel> ollama_models = new ArrayList<>();

        try {
            HttpResponse res= performRequestWithMontoya(montoyaApi, this.endpoint + Endpoints.models, null, "GET", null, this.debug);
            if (res!=null) {
                String jsonString = res.bodyToString();

                ObjectMapper objectMapper = new ObjectMapper();
                List<OllamaModel> modelValues = new ArrayList<>();


                JsonNode rootNode = objectMapper.readTree(jsonString);
                JsonNode modelsNode = rootNode.get("models");

                if (modelsNode != null && modelsNode.isArray()) {
                    // Iterate over each element in the "models" array
                    for (JsonNode modelEntry : modelsNode) {
                        JsonNode modelValueNode = modelEntry.get("model");
                        if (modelValueNode != null && modelValueNode.isTextual()) {
                            ollama_models.add(new OllamaModel(modelValueNode.asText(), modelValueNode.asText()));
                        }
                    }
                }
            }
            else
            {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return ollama_models;


    }


    public String QueryLLM(String model,String prompt) {
        String response = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonObject = objectMapper.createObjectNode();
            jsonObject.put("model", model);
            jsonObject.put("prompt", "[INST] "+prompt+" [/INST]");
            jsonObject.put("raw", true);
            jsonObject.put("stream", false);
            String jsonData = objectMapper.writeValueAsString(jsonObject);

            HttpResponse res= performRequestWithMontoya(montoyaApi, this.endpoint + Endpoints.queryLLM, jsonData, "POST", null, this.debug);

            String jsonResp =res.bodyToString();
            ObjectMapper objectresponse = new ObjectMapper();
            JsonNode rootNode = objectresponse.readTree(jsonResp);
            response = rootNode.get("response").asText();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }



    public String QueryLLMLangchain(String modelName,String prompt,Double temperature)
    {
        String response = "";
        try
        {
            ChatModel model = OllamaChatModel.builder().baseUrl(this.endpoint).modelName(modelName).
                    logRequests(true).logResponses(true).
                    timeout(Duration.ofSeconds(Config.LLMtimeout)).maxRetries(Config.LLMmaxRetries).
                    responseFormat(ResponseFormat.TEXT).temperature(temperature).build();
            response = model.chat(prompt);

            //filter out <think></think> tags that some chain-of-thought (CoT) models use, e.g Deepseek
            Pattern THINK_TAG_PATTERN = Pattern.compile("<think>.*?</think>", Pattern.DOTALL);
            Matcher matcher = THINK_TAG_PATTERN.matcher(response);
            response= matcher.replaceAll("").trim();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }



}
