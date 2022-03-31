package com.bilgehankalay.gelirgidertakibi.DAO

import androidx.room.*
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi


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

}