package com.origin.commons.callerid.di

import com.origin.commons.callerid.db.CallEndDB
import com.origin.commons.callerid.db.dao.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideReminderDao(db: CallEndDB): ReminderDao = db.reminderDao()
}