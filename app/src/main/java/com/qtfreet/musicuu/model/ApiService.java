package com.qtfreet.musicuu.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by qtfreet on 2016/3/25.
 */
public interface ApiService {
    @GET("search/song")
    Call<List<resultBean>> GetInfo(@Query("key") String key, @Query("type") String type);
}
