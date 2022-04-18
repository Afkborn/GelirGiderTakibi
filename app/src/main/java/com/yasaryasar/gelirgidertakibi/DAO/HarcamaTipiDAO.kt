package com.yasaryasar.gelirgidertakibi.DAO

import androidx.room.*
import com.yasaryasar.gelirgidertakibi.Model.HarcamaTipi


@Dao
interface HarcamaTipiDAO {
    @Insert
    fun harcamaTipiEkle(harcamaTipi: HarcamaTipi)

    @Update
    fun harcamaTipiGuncelle(harcamaTipi: HarcamaTipi)

    @Delete
    fun harcamaTipiSil(harcamaTipi: HarcamaTipi)

    @Query("SELECT * FROM harcama_tipleri")
    fun tumHarcamaTipi() : List<HarcamaTipi?>

    @Query("SELECT * FROM harcama_tipleri WHERE ad =:harcamaAd")
    fun harcamaTipiGetirAd(harcamaAd : String) : HarcamaTipi?

    @Query("SELECT * FROM harcama_tipleri WHERE id =:harcamaId" )
    fun harcamaTipiGetirId(harcamaId : Int) : HarcamaTipi?

}