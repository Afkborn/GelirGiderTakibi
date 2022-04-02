package com.bilgehankalay.gelirgidertakibi.DAO

import androidx.room.*
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider

@Dao
interface GelirGiderDAO {
    @Insert
    fun gelirGiderEkle(gelirGider : GelirGider)

    @Update
    fun gelirGiderGuncelle(gelirGider: GelirGider)

    @Delete
    fun gelirGiderSil(gelirGider: GelirGider)

    @Query("SELECT * FROM gelir_gider ORDER BY eklenme_zamani DESC")
    fun tumGelirGider() : List<GelirGider?>




}