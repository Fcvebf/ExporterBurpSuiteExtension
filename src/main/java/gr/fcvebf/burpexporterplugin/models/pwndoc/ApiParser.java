package gr.fcvebf.burpexporterplugin.models.pwndoc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ApiParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> ApiResponse<List<T>> parseListResponse(HttpResponse<String> response, TypeReference<ApiResponse<List<T>>> typeRef) throws Exception {
        JsonNode rootNode = mapper.readTree(response.body());
        JsonNode datasNode = rootNode.get("datas");

        if (datasNode != null && datasNode.isArray()) {
            ApiResponse<List<T>> apiResponse = new ApiResponse<>();
            List<T> dataList = new ArrayList<>();

            // Get the actual type of the elements in the list (T)
            Type apiResponseType = typeRef.getType();
            if (apiResponseType instanceof ParameterizedType) {
                ParameterizedType parameterizedApiResponseType = (ParameterizedType) apiResponseType;
                Type listType = parameterizedApiResponseType.getActualTypeArguments()[0]; // This is List<T>

                if (listType instanceof ParameterizedType) {
                    ParameterizedType parameterizedListType = (ParameterizedType) listType;
                    Type elementType = parameterizedListType.getActualTypeArguments()[0]; // This is T

                    Class<T> elementClass = (Class<T>) elementType;

                    for (JsonNode node : datasNode) {
                        T item = mapper.readValue(node.toString(), elementClass);
                        dataList.add(item);
                    }
                    apiResponse.setDatas(dataList);
                    return apiResponse;
                }
            }
            throw new Exception("Could not determine the element type of the list.");
        } else {
            throw new Exception("The 'datas' property was not found or is not an array in the JSON response.");
        }
    }


    public static <T> ApiResponse<List<T>> parseListResponse(String responseBody, TypeReference<ApiResponse<List<T>>> typeRef) throws Exception {
        JsonNode rootNode = mapper.readTree(responseBody);
        JsonNode datasNode = rootNode.get("datas");

        if (datasNode != null && datasNode.isArray()) {
            ApiResponse<List<T>> apiResponse = new ApiResponse<>();
            List<T> dataList = new ArrayList<>();

            // Get the actual type of the elements in the list (T)
            Type apiResponseType = typeRef.getType();
            if (apiResponseType instanceof ParameterizedType) {
                ParameterizedType parameterizedApiResponseType = (ParameterizedType) apiResponseType;
                Type listType = parameterizedApiResponseType.getActualTypeArguments()[0]; // This is List<T>

                if (listType instanceof ParameterizedType) {
                    ParameterizedType parameterizedListType = (ParameterizedType) listType;
                    Type elementType = parameterizedListType.getActualTypeArguments()[0]; // This is T

                    Class<T> elementClass = (Class<T>) elementType;

                    for (JsonNode node : datasNode) {
                        T item = mapper.readValue(node.toString(), elementClass);
                        dataList.add(item);
                    }
                    apiResponse.setDatas(dataList);
                    return apiResponse;
                }
            }
            throw new Exception("Could not determine the element type of the list.");
        } else {
            throw new Exception("The 'datas' property was not found or is not an array in the JSON response.");
        }
    }



    public static ApiResponse<?> parseResponse(String endpoint, HttpResponse<String> response) throws Exception {
        return switch (endpoint) {
            case Endpoints.audit_types -> parseListResponse(response, new TypeReference<ApiResponse<List<AuditType>>>() {});
            case Endpoints.languages -> parseListResponse(response, new TypeReference<ApiResponse<List<Language>>>() {});
            case Endpoints.vulncategories -> parseListResponse(response, new TypeReference<ApiResponse<List<VulnCategory>>>() {});
            case Endpoints.vulntypes -> parseListResponse(response, new TypeReference<ApiResponse<List<VulnType>>>() {});
            default -> throw new IllegalArgumentException("Unknown endpoint: " + endpoint);
        };
    }



    public static ApiResponse<?> parseResponse(String endpoint, String responseBody) throws Exception {
        return switch (endpoint) {
            case Endpoints.audit_types -> parseListResponse(responseBody, new TypeReference<ApiResponse<List<AuditType>>>() {});
            case Endpoints.languages -> parseListResponse(responseBody, new TypeReference<ApiResponse<List<Language>>>() {});
            case Endpoints.vulncategories -> parseListResponse(responseBody, new TypeReference<ApiResponse<List<VulnCategory>>>() {});
            case Endpoints.vulntypes -> parseListResponse(responseBody, new TypeReference<ApiResponse<List<VulnType>>>() {});
            default -> throw new IllegalArgumentException("Unknown endpoint: " + endpoint);
        };
    }

}