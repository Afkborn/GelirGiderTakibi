package com.bilgehankalay.gelirgidertakibi.activity

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private lateinit var gelirGiderTakipDatabase : GelirGiderTakipDatabase

    var gelirGiderList  : List<HarcamaTipi?> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHostFragment.navController)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(applicationContext)!!

        gelirGiderList = gelirGiderTakipDatabase.harcamaTipiDAO().tumHarcamaTipi()



        if (gelirGiderList.isEmpty()){
            temelTipYukle()
        }

    }

    private fun saveImageToInternalStorage(drawableId:Int): Uri {
        // Get the image from drawable resource as drawable object
        val drawable = ContextCompat.getDrawable(applicationContext, drawableId)
        // Get the bitmap from drawable object
        val bitmap = (drawable as BitmapDrawable).bitmap

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)


        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)

    }


    private fun temelTipYukle(){
        val temelHarcamaTipleri  : ArrayList<HarcamaTipi?> = arrayListOf()

        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.market), drawable_name = "shopping_cart"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.yemek), drawable_name = "fastfood"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.kiyafet), drawable_name = "kiyafet"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.yol), drawable_name = "bus"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.ziynet), drawable_name = "ziynet"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.kredi), drawable_name = "credit_card"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.borsa), drawable_name = "currency_exchange"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.espor), drawable_name = "sports_esports"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.okul), drawable_name = "school"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.cocuk), drawable_name = "child_care"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.seyahat), drawable_name = "seyahat"))

        temelHarcamaTipleri.forEach {
            if(it != null) {
                if (gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiGetirAd(it.ad) == null)
                    gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiEkle(it)
            }
        }
    }




}