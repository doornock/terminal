package cz.sodae.doornock.terminal.httpApi;

import org.json.JSONObject;

import java.io.IOException;

public class ApiServerSender {

    ApiSender apiSender = new ApiSender();

    private String apiUrl;

    private String nodeId;

    private String apiKey;

    public ApiServerSender(String apiUrl, String nodeId, String apiKey) {
        this.apiUrl = apiUrl.replaceAll("/+$", "");
        this.nodeId = nodeId;
        this.apiKey = apiKey;
    }

    public JSONObject get(String url) throws IOException, ApiSender.ApiException {
        return apiSender.get(this.apiUrl + url);
    }


    /**
     * Sends unsigned POST http request (with JSON data) and it does not verification
     */
    public JSONObject post(String url, JSONObject post) throws IOException, ApiSender.ApiException {
        return apiSender.post(this.apiUrl + url, post);
    }


    /**
     * Sends signed GET http request and verify output
     */
    public JSONObject secureGet(String url) throws IOException, ApiSender.ApiException {
        return apiSender.get(this.apiUrl + url, this.nodeId, this.apiKey);
    }


    /**
     * Sends signed POST http request and verify output
     */
    public JSONObject securePost(String url, JSONObject post) throws IOException, ApiSender.ApiException {
        return apiSender.post(this.apiUrl + url, post, this.nodeId, this.apiKey);
    }
}
