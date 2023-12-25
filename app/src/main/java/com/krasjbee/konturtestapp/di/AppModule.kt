package com.krasjbee.konturtestapp.di

import android.content.Context
import com.krasjbee.konturtestapp.data.PersonRepositoryImpl
import com.krasjbee.konturtestapp.data.cache.PagingCache
import com.krasjbee.konturtestapp.data.cache.SharedPreferencesFetchTimeProvider
import com.krasjbee.konturtestapp.data.cache.TimedPagingCache
import com.krasjbee.konturtestapp.datasource.database.PersonDao
import com.krasjbee.konturtestapp.datasource.database.PersonDatabase
import com.krasjbee.konturtestapp.datasource.remote.NetworkClient
import com.krasjbee.konturtestapp.datasource.remote.PersonApiClient
import com.krasjbee.konturtestapp.domain.Person
import com.krasjbee.konturtestapp.domain.PersonRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun providePersonDao(@ApplicationContext context: Context): PersonDao =
        PersonDatabase.getInstance(context).personDao()

    @Provides
    @Singleton
    fun providePersonClient(): PersonApiClient {
        return PersonApiClient.create(NetworkClient.retrofit)
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {
    @Binds
    @Singleton
    abstract fun bindPersonRepository(impl: PersonRepositoryImpl): PersonRepository

    @Binds
    @Singleton
    abstract fun bindCache(impl : TimedPagingCache) : PagingCache<Person>

    @Binds
    @Singleton
    abstract fun bindTimeFetchProvider(impl: SharedPreferencesFetchTimeProvider) : PagingCache.FetchTimeProvider
}