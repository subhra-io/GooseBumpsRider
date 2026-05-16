package com.goosebumps.rider.data.local.db

import android.content.Context
import androidx.room.Room
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
    fun provideRiderDatabase(@ApplicationContext context: Context): RiderDatabase {
        return Room.databaseBuilder(
            context,
            RiderDatabase::class.java,
            "goosebumps_rider.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideOrderDao(db: RiderDatabase): OrderDao = db.orderDao()

    @Provides
    fun provideRiderProfileDao(db: RiderDatabase): RiderProfileDao = db.riderProfileDao()
}
