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

    public static final String API_URL = "http://10.100.102.10:8080";
    public class RunItem{
        private String id;
        public RunItem(){

        }
        public String getId() {
            return id;
        }
    }

    public static class getRegularResponse {
        public boolean isOk;
        public  String err;

        public getRegularResponse(boolean isOk,String err) {
            this.err = err;
            this.isOk = isOk;
        }
    }
    public static class MessageData {
        public String message;
        public String registrationId;
        public String title;

        public MessageData(String message, String token, String title) {
            this.message = message;
            this.registrationId = token;
            this.title = title;
        }
    }

    public static class FeedListRequest {
        public String title;
        public String userId;
        public String userName;

        public FeedListRequest(String title, String userId, String userName) {
            this.title = title;
            this.userId = userId;
            this.userName = title;
        }
    }
    public static class RecommendRunsRequest {
        public String userId;
        public  String langlat;

        public RecommendRunsRequest(String userId, String langlat) {
            this.userId = userId;
            this.langlat = langlat;
        }
    }
    public static class FeedRunsRequest {
        public  String userId;
        public String langtitude;
        public String latitude;

        public FeedRunsRequest(String userId, String lang,String lat) {
            this.userId = userId;
            this.langtitude = lang;
            this.latitude = lat;
        }
    }
    public static class UpdateAverageRequest {
        public String userId;
        public String runId;

        public UpdateAverageRequest(String userId,String runId){
            this.userId = userId;
            this.runId = runId;
    }
}

    public interface HttpBinService {


        // POST with a JSON body
//        @POST("/send")
//        Call<getRegularResponse> postWithJson(
//                @Body MessageData loginData
//        );
        @POST("/getHistoryRuns")
        Call<getRegularResponse> postGetHistory(
                @Body FeedListRequest loginData
        );

        @POST("/getRecommendedRuns")
        Call<getRegularResponse> postRecommendedRuns(@Body FeedRunsRequest data);

        @POST("/updateAverage")
        Call<getRegularResponse> postUpdateAverage(
                @Body UpdateAverageRequest loginData
        );
        @POST("/getFeed")
        Call<getRegularResponse> postFeed(
                @Body FeedRunsRequest loginData
        );
    }

}
