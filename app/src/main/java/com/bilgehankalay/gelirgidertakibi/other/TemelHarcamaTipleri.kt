package com.bilgehankalay.gelirgidertakibi.other

import android.content.Context
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.R

class TemelHarcamaTipleri(applicationContext: Context) : ArrayList<HarcamaTipi?>() {
    val temelHarcamaTipleri  : ArrayList<HarcamaTipi?> = arrayListOf()
    init {
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.market), drawable_name = "shopping_cart"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.yemek), drawable_name = "fastfood"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.kiyafet), drawable_name = "kiyafet"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.yol), drawable_name = "bus"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.ziynet), drawable_name = "ziynet"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.kredi), drawable_name = "credit_card"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.espor), drawable_name = "sports_esports"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.okul), drawable_name = "school"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.cocuk), drawable_name = "child_care"))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.seyahat), drawable_name = "seyahat"))
    }
}