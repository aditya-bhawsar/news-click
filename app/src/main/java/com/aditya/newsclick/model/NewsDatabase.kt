package com.aditya.newsclick.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Article::class], version = 1)
abstract class NewsDatabase :RoomDatabase(){ abstract fun getNewsDao():NewsDao }