package com.aditya.newsclick.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.newsclick.api.ResponseWrapper
import com.aditya.newsclick.model.Article
import com.aditya.newsclick.model.NewsResponse
import com.aditya.newsclick.repository.NewsRepository
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel @ViewModelInject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    val breakingNews : MutableLiveData<ResponseWrapper<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse :NewsResponse? = null


    val searchNews : MutableLiveData<ResponseWrapper<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse :NewsResponse? = null

    init {
        getNewsHeadlines("in")
    }

    fun getNewsHeadlines(countryCode: String) = viewModelScope.launch {
        safeBreakNewsCall(countryCode)
    }

    private fun handleNewsResponse(response: Response<NewsResponse>):ResponseWrapper<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { result->
                breakingNewsPage++
                if(breakingNewsResponse == null){
                    breakingNewsResponse = result
                }
                else{
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle= result.articles

                    oldArticle?.addAll(newArticle)
                }
                return ResponseWrapper.Success(breakingNewsResponse?: result)
            }
        }
        return ResponseWrapper.Error(response.message())
    }

    fun searchNews(query:String) = viewModelScope.launch {
        safeSearchNewsCall(query)
    }

    private fun handleSearchNews(response: Response<NewsResponse>):ResponseWrapper<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { result->
                searchNewsPage++
                if(searchNewsResponse==null){
                    searchNewsResponse= result
                }
                else{
                    val oldArticle = searchNewsResponse?.articles
                    val newArticle= result.articles

                    oldArticle?.addAll(newArticle)
                }
                return ResponseWrapper.Success(searchNewsResponse?: result)
            }
        }
        return ResponseWrapper.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.insertNews(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteNews(article)
    }

    fun getAllNews() = newsRepository.getNewsSaved()

    private suspend fun safeBreakNewsCall(countryCode: String){
        breakingNews.postValue(ResponseWrapper.Loading())
        try {
            if(newsRepository.hasConnection()){
                val response = newsRepository.getNewsHeadlines(countryCode,breakingNewsPage)
                breakingNews.postValue(handleNewsResponse(response))
            }else{
                breakingNews.postValue(ResponseWrapper.Error("No Connection"))
            }
        }
        catch (t:Throwable){
            when(t){
                is IOException -> breakingNews.postValue(ResponseWrapper.Error("Network Failure"))
                else-> breakingNews.postValue(ResponseWrapper.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeSearchNewsCall(query: String){
        searchNews.postValue(ResponseWrapper.Loading())
        try {
            if(newsRepository.hasConnection()){
                val response = newsRepository.getSearchedNews(query,breakingNewsPage)
                searchNews.postValue(handleSearchNews(response))
            }else{
                searchNews.postValue(ResponseWrapper.Error("No Connection"))
            }
        }
        catch (t:Throwable){
            when(t){
                is IOException -> searchNews.postValue(ResponseWrapper.Error("Network Failure"))
                else-> searchNews.postValue(ResponseWrapper.Error("Conversion Error"))
            }
        }
    }

}