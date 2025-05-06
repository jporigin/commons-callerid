package com.origin.commons.callerid.di

import android.content.Context
import androidx.room.Room
import com.origin.commons.callerid.db.CallEndDB
import com.origin.commons.callerid.db.CallEndDB.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CallEndDB {
        return Room.databaseBuilder(context, CallEndDB::class.java, DATABASE_NAME)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration().build()
    }

}