package com.bilgehankalay.gelirgidertakibi.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "harcama_tipleri")
data class HarcamaTipi(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Int = 0,

    @ColumnInfo(name="ad")
    var ad : String,

    @ColumnInfo(name = "icon_id")
    var icon_id : Int

) : Serializable
