package com.bilgehankalay.gelirgidertakibi.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bilgehankalay.gelirgidertakibi.DAO.GelirGiderDAO
import com.bilgehankalay.gelirgidertakibi.DAO.HarcamaTipiDAO
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi

@Database(entities = [GelirGider::class,HarcamaTipi::class], version = 10)
abstract class GelirGiderTakipDatabase : RoomDatabase() {
    abstract fun gelirGiderDAO() : GelirGiderDAO
    abstract fun harcamaTipiDAO() : HarcamaTipiDAO

    companion object{
        private var INSTANCE : GelirGiderTakipDatabase? = null
        fun getirGelirGiderTakipDatabase(context : Context) : GelirGiderTakipDatabase?{
            if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    GelirGiderTakipDatabase::class.java,
                    "gelirgidertakip.db"
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
            return INSTANCE
        }
    }
}