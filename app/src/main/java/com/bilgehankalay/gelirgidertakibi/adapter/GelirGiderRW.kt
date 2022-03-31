package com.bilgehankalay.gelirgidertakibi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.GelirGiderCardTasarimBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GelirGiderRW(private var gelirGiderList : ArrayList<GelirGider>)  : RecyclerView.Adapter<GelirGiderRW.GelirGiderCardTasarim>() {
    var toplamGelir = 0.0
    var toplamGider = 0.0
    class GelirGiderCardTasarim(val gelirGiderCardTasarimBinding : GelirGiderCardTasarimBinding) : RecyclerView.ViewHolder(gelirGiderCardTasarimBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GelirGiderCardTasarim {
        val layoutInflater = LayoutInflater.from(parent.context)
        val gelirGiderCardTasarimBinding = GelirGiderCardTasarimBinding.inflate(layoutInflater,parent,false)
        return GelirGiderCardTasarim(gelirGiderCardTasarimBinding)
    }

    override fun onBindViewHolder(holder: GelirGiderCardTasarim, position: Int) {
        val gelirGider = gelirGiderList[position]
        holder.gelirGiderCardTasarimBinding.also {




            val formatlananMiktar = NumberFormat.getCurrencyInstance(Locale("tr","TR")).format(gelirGider.miktar)
            if (gelirGider.tip == 0){

                //GELİR
                it.imageViewGelirGiderIco.visibility = View.INVISIBLE
                it.imageViewHarcamaTipiIco.setImageResource(R.drawable.down_arrow)
                it.textViewHarcamaTipiAdi.visibility = View.INVISIBLE
                it.textViewHarcamaMiktar.text = "+${formatlananMiktar}"
                it.progressBarHarcamaYuzde.max = toplamGelir.toInt()

                val harcamaYuzde =  gelirGider.miktar / (toplamGelir / 100 )
                val yuvarlananSayi = String.format("%.2f", harcamaYuzde)
                it.textViewHarcamaYuzde.text = "%${yuvarlananSayi}"


            }
            else{
                //GİDER
                it.imageViewGelirGiderIco.visibility = View.VISIBLE
                it.imageViewGelirGiderIco.setImageResource(R.drawable.up_arrow)
                it.textViewHarcamaTipiAdi.visibility = View.VISIBLE

                it.textViewHarcamaMiktar.text = "-${formatlananMiktar}"
                it.progressBarHarcamaYuzde.max = toplamGider.toInt()
                val harcamaYuzde =  gelirGider.miktar / (toplamGider / 100 )
                val yuvarlananSayi = String.format("%.2f", harcamaYuzde)
                it.textViewHarcamaYuzde.text = "%${yuvarlananSayi}"


                it.textViewHarcamaTipiAdi.text = gelirGider.harcama_tipi_id.toString() // TODO HARCAMA TİPİ İD İLE HARCAMA ADI BUL
                it.imageViewHarcamaTipiIco.setImageResource(R.drawable.basket) // TODO çöz

            }

            it.progressBarHarcamaYuzde.progress = gelirGider.miktar.toInt()
            it.textViewHarcamaAdi.text = gelirGider.ad

            //long to date
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val date = Date(gelirGider.eklenme_zamani * 1000)
            it.textViewHarcamaTarih.text = sdf.format(date)


        }
    }

    override fun getItemCount(): Int {
        return gelirGiderList.size
    }

    fun toplamGelirGiderHesapla() : Pair<Double,Double>{
        gelirGiderList.forEach {
            if (it.tip == 0)
                toplamGelir += it.miktar
            else
                toplamGider += it.miktar
        }
        return Pair(toplamGelir,toplamGider)
    }

}