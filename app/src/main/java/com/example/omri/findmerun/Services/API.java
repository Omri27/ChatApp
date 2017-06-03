package com.example.omri.findmerun.Services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public class API {

    public static final String API_URL = "http://10.100.102.4:8080";

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
    public static class UpComingListRequest {
        public String userId;

        public UpComingListRequest(String userId) {

            this.userId = userId;
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
        @POST("/getComingUpRuns")
        Call<getRegularResponse> postComingUpRuns(
                @Body UpComingListRequest loginData
        );
    }

}
