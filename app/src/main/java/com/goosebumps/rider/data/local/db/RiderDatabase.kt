package com.goosebumps.rider.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.goosebumps.rider.data.local.entity.CachedOrderEntity
import com.goosebumps.rider.data.local.entity.RiderProfileEntity

@Database(
    entities = [CachedOrderEntity::class, RiderProfileEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RiderDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun riderProfileDao(): RiderProfileDao
}
