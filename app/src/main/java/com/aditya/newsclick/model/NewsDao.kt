package com.aditya.newsclick.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(article: Article) :Long

    @Query("SELECT * FROM articles WHERE title = :title")
    fun getArticleIfAny(title:String): LiveData<Article?>

    @Delete
    suspend fun deleteNews(article: Article)

    @Query("SELECT * FROM articles")
    fun getAllNews():LiveData<List<Article>>
}