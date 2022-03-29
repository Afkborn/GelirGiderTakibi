package com.bilgehankalay.gelirgidertakibi.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "gelirler")
data class Gelir(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Int = 0,

    @ColumnInfo(name = "ad")
    var ad : String,

    @ColumnInfo(name = "miktar")
    var miktar : Double,

    @ColumnInfo(name = "aciklama")
    var aciklama : String?,

    @ColumnInfo(name="eklenme_zamani")
    var eklenme_zamani : Long,

    @ColumnInfo(name = "duzenli_mi")
    var duzenli_mi : Boolean?,

    @ColumnInfo(name = "tekrar_suresi")
    var tekrar_suresi : Int?,

    ) : Serializable
