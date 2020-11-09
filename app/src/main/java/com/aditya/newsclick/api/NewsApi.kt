package com.aditya.newsclick.api

import com.aditya.newsclick.model.NewsResponse
import com.aditya.newsclick.other.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getNewsHeadlines(
        @Query("country")countryCode:String = "in",
        @Query("page")pageNo:Int = 1,
        @Query("apiKey")apiKey:String = API_KEY
    ):Response<NewsResponse>

    @GET("v2/everything")
    suspend fun getSearchedNews(
        @Query("q")search:String,
        @Query("page")pageNo:Int = 1,
        @Query("apiKey")apiKey:String = API_KEY
    ):Response<NewsResponse>
}