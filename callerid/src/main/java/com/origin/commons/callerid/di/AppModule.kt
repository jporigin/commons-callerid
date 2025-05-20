package com.origin.commons.callerid.di

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import androidx.core.content.getSystemService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    fun provideResources(application: Application): Resources = application.resources

    @Provides
    fun providesNotificationManager(application: Application) =
        application.getSystemService<NotificationManager>()!!

    @Provides
    fun providesAlarmManager(application: Application) = application.getSystemService<AlarmManager>()!!

}