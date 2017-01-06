package com.example.omri.chatapp;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public class API {

    public static final String API_URL = "http://46.101.226.56:8080";
    static class HttpBinResponse {
        // the request url
        String url;

        // the requester ip
        String origin;

        // all headers that have been sent
        Map headers;

        // url arguments
        Map args;

        // post form parameters
        Map form;

        // post body json
        Map json;
    }
    static class MessageData {
        String message;
        String registrationId;
        String title;

        public MessageData(String message, String token, String title) {
            this.message = message;
            this.registrationId = token;
            this.title = title;
        }
    }
    public interface HttpBinService {


        // POST with a JSON body
        @POST("/send")
        Call<HttpBinResponse> postWithJson(
                @Body MessageData loginData
        );
    }

}
