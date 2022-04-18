package com.yasaryasar.gelirgidertakibi.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "gelir_gider") // tablo adı
data class GelirGider(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id : Int = 0,

    @ColumnInfo(name = "tip")
    var tip : Int, // 0 => Gelir, 1 => Gider, 3 => TekrarGelirGider

    @ColumnInfo(name = "ad")
    var ad : String? = null,

    @ColumnInfo(name = "miktar")
    var miktar : Double,

    @ColumnInfo(name = "aciklama")
    var aciklama : String? = null,

    @ColumnInfo(name="eklenme_zamani")
    var eklenme_zamani : Long,

    @ColumnInfo(name="harcama_tipi")
    var harcama_tipi_id : Int? = null,

    @ColumnInfo(name = "duzenli_mi")
    var duzenli_mi : Boolean? = null,

    @ColumnInfo(name = "tekrar_tipi")
    var tekrar_tipi : Int? = null,
    //0 => Hergün, 1 => Hafta içi, 2 => Hafta sonu, 3 => Her Hafta, 4 => Her 2 haftada bir, 5 => Her ay

    @ColumnInfo(name = "aktif_pasif")
    var aktif_pasif : Boolean? = null, // True => Aktif, False = Pasif

    @ColumnInfo(name = "ana_harcama")
    var ana_harcama : Int? = null,

    @ColumnInfo(name = "bitis_tarihi")
    var bitis_tarihi : Long? = null,

    @ColumnInfo(name = "eklenmis_mi")
    var eklenmis_mi : Boolean? = null,

    @ColumnInfo(name = "yinelenen_mi")
    var yinelenen_mi : Boolean? = null


) : Serializable
