package com.example.fshop.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothDao {
    @Insert
    fun insertCloth(cloth: ClothItem)

    @Query("SELECT * FROM cloth")
    fun getAllCloth(): Flow<List<ClothItem>>

    @Update
    fun updateCloth(user: ClothItem)

    @Query("SELECT * FROM Cloth WHERE name = :clothName")
    suspend fun getClothesByName(clothName: String): List<ClothItem>

    @Query("""
    SELECT c.*
    FROM cloth c
    INNER JOIN (
        SELECT name, MIN(size) as minSize
        FROM cloth
        GROUP BY name
    ) groupedCloth
    ON c.name = groupedCloth.name AND c.size = groupedCloth.minSize
""")
    fun getUniqueNameLowestSize(): Flow<List<ClothItem>>

//    @Query("SELECT * FROM users WHERE is_auth = :isAuth LIMIT 1")
//    suspend fun getUserByAuthStatus(isAuth: Int): UserItem?
//
//    @Query("SELECT * FROM users WHERE is_auth = 1 LIMIT 1")
//    fun getCurrentUser(): UserItem?


}