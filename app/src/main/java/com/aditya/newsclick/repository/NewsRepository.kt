package com.aditya.newsclick.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.aditya.newsclick.api.NewsApi
import com.aditya.newsclick.model.Article
import com.aditya.newsclick.model.NewsDao
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NewsRepository @Inject constructor(val newsDao:NewsDao, val newsApi: NewsApi, @ApplicationContext val ctx:Context) {

    suspend fun insertNews(article: Article) =  newsDao.insertNews(article)
    suspend fun deleteNews(article: Article) =  newsDao.deleteNews(article)
    suspend fun getNewsHeadlines(countryCode :String,pageNo: Int) = newsApi.getNewsHeadlines(countryCode,pageNo)
    suspend fun getSearchedNews(query: String, pageNo: Int) = newsApi.getSearchedNews(query,pageNo)
    fun getArticle(title: String) = newsDao.getArticleIfAny(title)
    fun getNewsSaved() = newsDao.getAllNews()

    fun hasConnection():Boolean{
        val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork  =connectivityManager.activeNetwork?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)-> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)-> true
                else->false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> return true
                    ConnectivityManager.TYPE_MOBILE -> return true
                    ConnectivityManager.TYPE_ETHERNET -> return true
                    else->false
                }
            }
        }
        return false
    }
}