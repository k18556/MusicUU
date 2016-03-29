package com.qtfreet.musicuu.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by qtfreet on 2016/3/25.
 */
public interface ApiService {
    @GET("music/search/{type}/{key}/1")
    Call<List<resultBean>> GetInfo(@Path("type") String type, @Path("key") String key, @Query("format") String format);

    @GET("vol/all")
    Call<List<RecomendResult>> GetRec(@Query("t") String t, @Query("p") String f, @Query("f") String json);
}
