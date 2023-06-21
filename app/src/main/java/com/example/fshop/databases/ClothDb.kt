package com.example.fshop.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ClothItem::class], version = 1)
abstract class ClothDb : RoomDatabase() {
    abstract fun getDao(): ClothDao

    companion object{
        fun getDb(context: Context): ClothDb {
            return Room.databaseBuilder(
                context.applicationContext,
                ClothDb::class.java,
                "cloth.db"
            ).build()
        }
    }
}