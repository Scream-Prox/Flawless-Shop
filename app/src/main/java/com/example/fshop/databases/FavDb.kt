package com.example.fshop.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavItem::class], version = 1)
abstract class FavDb : RoomDatabase() {
    abstract fun getDao(): FavDao

    companion object{
        fun getDb(context: Context): FavDb {
            return Room.databaseBuilder(
                context.applicationContext,
                FavDb::class.java,
                "favourite.db"
            ).build()
        }
    }
}