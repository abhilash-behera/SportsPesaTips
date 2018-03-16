
package com.football.predictions.retrofit;

import com.football.predictions.Game;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PhoneTodayResponse {

    @SerializedName("data")
    @Expose
    private List<Game> data;

    public ArrayList<Game> getData(){
        return (ArrayList<Game>)this.data;
    }
}
