package com.yasaryasar.gelirgidertakibi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yasaryasar.gelirgidertakibi.Model.HarcamaTipi
import com.yasaryasar.gelirgidertakibi.R
import com.yasaryasar.gelirgidertakibi.databinding.LegendCardTasarimBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class LegendRW (private var harcamaTipleriList : ArrayList<HarcamaTipi>) : RecyclerView.Adapter<LegendRW.LegendCardTasarim>() {
    private  lateinit var  context : Context
    class LegendCardTasarim(val legendCardTasarimBinding : LegendCardTasarimBinding) : RecyclerView.ViewHolder(legendCardTasarimBinding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegendCardTasarim {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val legendCardTasarimBinding = LegendCardTasarimBinding.inflate(layoutInflater,parent,false)
        return LegendCardTasarim(legendCardTasarimBinding)
    }

    override fun onBindViewHolder(holder: LegendCardTasarim, position: Int) {
        val seciliKategori = harcamaTipleriList[position]
        holder.legendCardTasarimBinding.let {
            if (seciliKategori.chartColor != null){
                it.constraintLayoutCard.setBackgroundColor(seciliKategori.chartColor!!)
                it.textViewLegendAd.setText(seciliKategori.ad)

                val lang = context.getString(R.string.lang)
                val country = context.getString(R.string.country)
                val formatlananMiktar = NumberFormat.getCurrencyInstance(Locale(lang,country)).format(seciliKategori.kategoriToplamHarcamaMiktar!!)
                it.textViewLegendYuzde.setText(formatlananMiktar)
            }

        }
    }

    override fun getItemCount(): Int {
        return harcamaTipleriList.size
    }
}