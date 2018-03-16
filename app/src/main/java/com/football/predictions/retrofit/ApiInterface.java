package com.football.predictions.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by Abhilash on 17-09-2017
 */

public interface ApiInterface {
    @Headers("Content-type:application/json")
    @GET("/phonehome_json.php")
    Call<PhoneHomeResponse> getPhoneHomeGames();

    @Headers("Content-type:application/json")
    @GET("/phonetoday_json.php")
    Call<PhoneTodayResponse> getPhoneTodayGames();

    @Headers("Content-type:application/json")
    @GET("/phoneprevious_json.php")
    Call<PhonePreviousResponse> getPhonePreviousGames();
}
