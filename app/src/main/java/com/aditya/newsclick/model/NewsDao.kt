package com.aditya.newsclick.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(article: Article) :Long

    @Delete
    suspend fun deleteNews(article: Article)

    @Query("SELECT * FROM articles")
    fun getAllNews():LiveData<List<Article>>
}