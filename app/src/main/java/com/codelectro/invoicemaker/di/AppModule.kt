package com.codelectro.invoicemaker.di

import android.content.Context
import androidx.room.Room
import com.codelectro.invoicemaker.db.MainDatabase
import com.codelectro.invoicemaker.util.Constant.MAIN_DATABASE_NAME

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMainDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        MainDatabase::class.java,
        MAIN_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideMainDao(db: MainDatabase) = db.getMainDao()


}