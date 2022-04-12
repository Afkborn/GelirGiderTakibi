package com.bilgehankalay.gelirgidertakibi.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.ActivityMainBinding
import com.bilgehankalay.gelirgidertakibi.other.TemelHarcamaTipleri
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private lateinit var gelirGiderTakipDatabase : GelirGiderTakipDatabase

    private lateinit var classTemelHarcamaTipleri: TemelHarcamaTipleri

    var harcamaList  : List<HarcamaTipi?> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)

        createDrawableFolder()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHostFragment.navController)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(applicationContext)!!
        classTemelHarcamaTipleri = TemelHarcamaTipleri(applicationContext)
        harcamaList = gelirGiderTakipDatabase.harcamaTipiDAO().tumHarcamaTipi()

        temelTipYukle()

    }

    private fun temelTipYukle(){
        val temelHarcamaTipleri = classTemelHarcamaTipleri.temelHarcamaTipleri
        temelHarcamaTipleri.forEach {
            if(it != null) {
                if (gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiGetirAd(it.ad) == null)
                    gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiEkle(it)
            }
        }
    }

    private fun createDrawableFolder(){
        val path = Environment.getDataDirectory()
        val file = File(path,"/data/com.bilgehankalay.gelirgidertakibi/drawable/")
        file.mkdir()

    }





}