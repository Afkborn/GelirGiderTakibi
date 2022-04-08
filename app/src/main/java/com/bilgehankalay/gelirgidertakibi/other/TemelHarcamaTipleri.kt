package com.bilgehankalay.gelirgidertakibi.other

import android.content.Context
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.R

class TemelHarcamaTipleri(applicationContext: Context) : ArrayList<HarcamaTipi?>() {
    val temelHarcamaTipleri  : ArrayList<HarcamaTipi?> = arrayListOf()
    init {
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.market), drawable_name = "shopping_cart", has_drawable = true, is_custom = false))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.yemek), drawable_name = "fastfood", has_drawable = true, is_custom = false))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.kiyafet), drawable_name = "kiyafet", has_drawable = true, is_custom = false))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.yol), drawable_name = "bus", has_drawable = true, is_custom = false))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.ziynet), drawable_name = "ziynet", has_drawable = true, is_custom = false))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.kredi), drawable_name = "credit_card", has_drawable = true, is_custom = false))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.espor), drawable_name = "sports_esports", has_drawable = true, is_custom = false))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.okul), drawable_name = "school", has_drawable = true, is_custom = false))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.cocuk), drawable_name = "child_care", has_drawable = true, is_custom = false))
        temelHarcamaTipleri.add(HarcamaTipi(ad = applicationContext.getString(R.string.seyahat), drawable_name = "seyahat", has_drawable = true, is_custom = false))

    }
}