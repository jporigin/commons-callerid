package com.origin.commons.callerid.di

import com.origin.commons.callerid.repository.ReminderRepository
import com.origin.commons.callerid.repository.ReminderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface AppBindModule {

    @Binds
    fun bindsReminderRepository(reminderRepositoryImpl: ReminderRepositoryImpl): ReminderRepository
}