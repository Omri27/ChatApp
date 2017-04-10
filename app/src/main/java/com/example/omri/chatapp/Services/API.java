package com.example.omri.chatapp.Services;

import com.example.omri.chatapp.Entities.Item;
import com.example.omri.chatapp.Entities.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public class API {

    public static final String API_URL = "http://10.100.102.13:8080";
    public class RunItem{
        private String id;
        public RunItem(){

        }
        public String getId() {
            return id;
        }
    }

    public static class HttpBinResponse {
        // the request url
        String url;

        // the requester ip
        String origin;

        // all headers that have been sent
        Map headers;

        // url arguments
        private  Map args;

        // post form parameters
        Map form;

        // post body json
        Map json;


    }
    public static class MessageData {
        String message;
        String registrationId;
        String title;

        public MessageData(String message, String token, String title) {
            this.message = message;
            this.registrationId = token;
            this.title = title;
        }
    }

    public static class FeedListRequest {
        String title;
        String userId;
        String userName;

        public FeedListRequest(String title, String userId, String userName) {
            this.title = title;
            this.userId = userId;
            this.userName = title;
        }
    }
    public static class RecommendRunsRequest {
        String userId;
        String langlat;

        public RecommendRunsRequest(String userId, String langlat) {
            this.userId = userId;
            this.langlat = langlat;
        }
    }
    public interface HttpBinService {


        // POST with a JSON body
        @POST("/send")
        Call<HttpBinResponse> postWithJson(
                @Body MessageData loginData
        );
        @POST("/getHistoryRuns")
        Call<HttpBinResponse> postGetHistory(
                @Body FeedListRequest loginData
        );

        @POST("/getRecommendedRuns")
        Call<List<RunItem>> postRecommendedRuns(@Body RecommendRunsRequest data);
    }

}
