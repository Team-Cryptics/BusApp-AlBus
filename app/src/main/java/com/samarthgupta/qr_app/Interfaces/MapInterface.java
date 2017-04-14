package com.samarthgupta.qr_app.Interfaces;

import com.samarthgupta.qr_app.POJO.MapData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by samarthgupta on 12/04/17.
 */

public interface MapInterface {


    @GET("/maps/api/distancematrix/json?")
    Call<MapData> getMapData(@Query("origins") String start, @Query("destinations") String end,@Query("mode") String mode,@Query("key") String apikey);


}
