package com.example.fshop.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDao {
    @Insert
    fun insertFavourite(favourite: FavItem)

    @Query("SELECT * FROM favourite")
    fun getAllFavourite(): Flow<List<FavItem>>

    @Update
    fun updateFavourite(favourite: FavItem) //

    @Query("SELECT * FROM favourite WHERE user_info = :userEmail")
    fun getFavouriteByUser(userEmail: String): Flow<List<FavItem>>

    @Query("DELETE FROM favourite WHERE id = :favouriteId")
    suspend fun deleteFavItem(favouriteId: Int)

    @Query("DELETE FROM favourite WHERE user_info = :userEmail")
    suspend fun deleteFavItemByUser(userEmail: String)

    @Query("SELECT * FROM favourite WHERE user_info = :userEmail")
    fun getFavItemByUser(userEmail: String): Flow<List<FavItem>>
}
