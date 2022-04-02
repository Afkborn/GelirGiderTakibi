package com.bilgehankalay.gelirgidertakibi.Model

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
    var tip : Int, // 0 => Gelir, 1 => Gider

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

    @ColumnInfo(name = "tekrar_suresi")
    var tekrar_suresi : Int? = null,

    @ColumnInfo(name = "aktif_pasif")
    var aktif_pasif : Boolean? = null, // True => Aktif, False = Pasif




) : Serializable {


}
