package com.aditya.newsclick.di

import android.content.Context
import androidx.room.Room
import com.aditya.newsclick.api.NewsApi
import com.aditya.newsclick.model.NewsDatabase
import com.aditya.newsclick.other.Constants.Companion.API_BASE_URL
import com.aditya.newsclick.other.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx:Context) = Room.databaseBuilder(
        ctx,
        NewsDatabase::class.java,
        DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideNewsDao(database: NewsDatabase) = database.getNewsDao()

    @Singleton
    @Provides
    fun provideRetrofit() = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    @Singleton
    @Provides
    fun provideNewsApi(retrofit: Retrofit) = retrofit.create(NewsApi::class.java)
}