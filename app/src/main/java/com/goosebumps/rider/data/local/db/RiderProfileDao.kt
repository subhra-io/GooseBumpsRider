package com.goosebumps.rider.data.local.db

import androidx.room.*
import com.goosebumps.rider.data.local.entity.RiderProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RiderProfileDao {

    @Query("SELECT * FROM rider_profile LIMIT 1")
    fun getProfile(): Flow<RiderProfileEntity?>

    @Query("SELECT * FROM rider_profile LIMIT 1")
    suspend fun getProfileOnce(): RiderProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: RiderProfileEntity)

    @Query("DELETE FROM rider_profile")
    suspend fun clearProfile()
}
