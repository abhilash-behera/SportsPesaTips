package com.football.predictions.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by Abhilash on 17-09-2017
 */

public interface ApiInterface {

    //Previous Games
    @Headers("Content-type:application/json")
    @GET("/phoneprevious_json.php")
    Call<PhonePreviousResponse> getPhonePreviousGames();

    @Headers("Content-type:application/json")
    @GET("/phoneprevious_sure_json.php")
    Call<PhonePreviousResponse> getPhonePreviousSure();

    @Headers("Content-type:application/json")
    @GET("/phoneprevious_vip_json.php")
    Call<PhonePreviousResponse> getPhonePreviousVip();


    //Today's Games
    @Headers("Content-type:application/json")
    @GET("/phonetoday_json.php")
    Call<PhoneTodayResponse> getPhoneTodayGames();

    @Headers("Content-type:application/json")
    @GET("/phonetoday_vip_json.php")
    Call<PhoneTodayResponse> getPhoneTodayVip();

    @Headers("Content-type:application/json")
    @GET("/phone_sure_tips_json.php")
    Call<PhoneTodayResponse> getSureTips();

    //Tomorrow's Games
    @Headers("Content-type:application/json")
    @GET("/phonetomorrow_json.php")
    Call<PhoneTodayResponse> getPhoneTomorrowsGames();

    @Headers("Content-type:application/json")
    @GET("/phonetomorrow_vip_json.php")
    Call<PhoneTodayResponse> getPhoneTomorrowsVip();

}
