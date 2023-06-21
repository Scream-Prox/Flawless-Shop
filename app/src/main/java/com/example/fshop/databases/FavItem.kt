package com.example.fshop.databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")
data class FavItem (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "price")
    var price: String,
    @ColumnInfo(name = "size")
    var size: String,
    @ColumnInfo(name = "text")
    var text: String? = null,
    @ColumnInfo(name = "user_info")
    var user_info: String,
    @ColumnInfo(name = "imageRes")
    var imageRes: String? = null
)