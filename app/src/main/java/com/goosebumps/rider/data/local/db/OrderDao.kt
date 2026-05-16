package com.goosebumps.rider.data.local.db

import androidx.room.*
import com.goosebumps.rider.data.local.entity.CachedOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM cached_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<CachedOrderEntity>>

    @Query("SELECT * FROM cached_orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): CachedOrderEntity?

    @Query("SELECT * FROM cached_orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<CachedOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: CachedOrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<CachedOrderEntity>)

    @Update
    suspend fun updateOrder(order: CachedOrderEntity)

    @Query("DELETE FROM cached_orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: String)

    @Query("DELETE FROM cached_orders")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM cached_orders WHERE status = 'DELIVERED'")
    suspend fun getDeliveredCount(): Int
}
