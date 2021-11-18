package com.app.azovatask.network

import com.app.azovatask.model.NewsModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url
import java.net.URL

interface APIService {

    @GET
    fun getNewsFeed(
        @Url url: String
    ): Call<NewsModel>

}