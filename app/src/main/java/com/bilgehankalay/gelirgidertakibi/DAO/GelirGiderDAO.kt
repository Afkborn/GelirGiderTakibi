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

    @Query("SELECT * FROM gelir_gider WHERE duzenli_mi = 1")
    fun duzenliGelirGider() : List<GelirGider?>

    @Query("SELECT * FROM gelir_gider WHERE harcama_tipi =:harcamaTipiId")
    fun harcamaTipiGelirGider(harcamaTipiId : Int) : List<GelirGider?>

    @Query("SELECT * FROM gelir_gider WHERE tip = 0")
    fun gelirlerim() : List<GelirGider?>

    @Query("SELECT * FROM gelir_gider WHERE tip = 1")
    fun giderlerim() : List<GelirGider?>

    @Query("SELECT * FROM gelir_gider WHERE eklenme_zamani =:eklenmeZamani")
    fun eklenmeZamaniGelirGider(eklenmeZamani : Long) : GelirGider?

    @Query("SELECT * FROM gelir_gider WHERE tip != 3 ORDER BY eklenme_zamani DESC")
    fun tipBirIkiGelirGider() : List<GelirGider?>

    @Query("SELECT * FROM gelir_gider WHERE id = :id")
    fun idGelirGider(id : Int) : GelirGider?

    @Query("SELECT eklenme_zamani FROM gelir_gider")
    fun tumGelirGiderTarih() : List<Long?>

    @Query("SELECT * FROM gelir_gider WHERE eklenme_zamani BETWEEN :startUnix AND :stopUnix")
    fun tumGelirGiderAy(startUnix : Long , stopUnix : Long) : List<GelirGider?>

}