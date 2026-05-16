package com.goosebumps.rider.di

import com.goosebumps.rider.data.repository.AuthRepositoryImpl
import com.goosebumps.rider.data.repository.OrderRepositoryImpl
import com.goosebumps.rider.data.repository.RiderRepositoryImpl
import com.goosebumps.rider.domain.repository.AuthRepository
import com.goosebumps.rider.domain.repository.OrderRepository
import com.goosebumps.rider.domain.repository.RiderRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds
    @Singleton
    abstract fun bindRiderRepository(impl: RiderRepositoryImpl): RiderRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}
