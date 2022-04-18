package com.bilgehankalay.gelirgidertakibi.Model

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "harcama_tipleri")
data class HarcamaTipi(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Int = 0,

    @ColumnInfo(name="ad")
    var ad : String,

    @ColumnInfo(name = "has_drawable")
    var has_drawable : Boolean,

    @ColumnInfo(name = "is_custom")
    var is_custom : Boolean,

    @ColumnInfo(name = "drawable_name")
    var drawable_name : String? = null,



) : Serializable {
    @Ignore
    var kategoriToplamHarcamaMiktar : Double? = null

    @Ignore
    var chartColor : Int? = null

    @Ignore
    var yuzde : Double? = null
}
