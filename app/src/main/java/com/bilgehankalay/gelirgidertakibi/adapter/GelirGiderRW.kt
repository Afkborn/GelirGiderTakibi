package com.bilgehankalay.gelirgidertakibi.adapter


import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.GelirGiderCardTasarimBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


class GelirGiderRW(private var gelirGiderList : ArrayList<GelirGider>)  : RecyclerView.Adapter<GelirGiderRW.GelirGiderCardTasarim>() {
    var toplamGelir = 0.0
    var toplamGider = 0.0
    var rw_parent : ViewGroup? = null

    private lateinit var gelirGiderTakipDatabase : GelirGiderTakipDatabase

    var onItemClick : (GelirGider) -> Unit = {}


    class GelirGiderCardTasarim(val gelirGiderCardTasarimBinding : GelirGiderCardTasarimBinding) : RecyclerView.ViewHolder(gelirGiderCardTasarimBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GelirGiderCardTasarim {
        val layoutInflater = LayoutInflater.from(parent.context)
        val gelirGiderCardTasarimBinding = GelirGiderCardTasarimBinding.inflate(layoutInflater,parent,false)
        rw_parent = parent
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(parent.context)!!
        return GelirGiderCardTasarim(gelirGiderCardTasarimBinding)
    }

    override fun onBindViewHolder(holder: GelirGiderCardTasarim, position: Int) {
        val gelirGider = gelirGiderList[position]
        holder.gelirGiderCardTasarimBinding.also {


            it.root.setOnClickListener {
                onItemClick(gelirGider)
            }

            val formatlananMiktar = NumberFormat.getCurrencyInstance(Locale("tr","TR")).format(gelirGider.miktar)
            if (gelirGider.tip == 0){

                it.imageViewGelirGiderIco.visibility = View.INVISIBLE
                it.imageViewHarcamaTipiIco.setImageResource(R.drawable.dollar)
                it.textViewHarcamaTipiAdi.visibility = View.INVISIBLE
                it.textViewHarcamaMiktar.text = rw_parent!!.context.getString(R.string.arti_bakiye,formatlananMiktar) // Resources.getSystem().getString(R.string.arti_bakiye,"1")
                it.progressBarHarcamaYuzde.max = toplamGelir.toInt()
                val harcamaYuzde =  gelirGider.miktar / (toplamGelir / 100 )
                val yuvarlananSayi = String.format("%.2f", harcamaYuzde)
                it.textViewHarcamaYuzde.text  = rw_parent!!.context.getString(R.string.harcama_yuzde,yuvarlananSayi)
                it.textViewHarcamaMiktar.setTextColor(Color.parseColor("#00cc00"))
            } //GELİR
            else{
                //GİDER
                it.imageViewGelirGiderIco.visibility = View.INVISIBLE
                //it.imageViewGelirGiderIco.setImageResource(R.drawable.up_arrow)
                it.textViewHarcamaTipiAdi.visibility = View.VISIBLE
                it.textViewHarcamaMiktar.text = rw_parent!!.context.getString(R.string.eksi_bakiye,formatlananMiktar)
                it.progressBarHarcamaYuzde.max = toplamGider.toInt()
                val harcamaYuzde =  gelirGider.miktar / (toplamGider / 100 )
                val yuvarlananSayi = String.format("%.2f", harcamaYuzde)
                it.textViewHarcamaYuzde.text  = rw_parent!!.context.getString(R.string.harcama_yuzde,yuvarlananSayi)
                it.textViewHarcamaMiktar.setTextColor(Color.parseColor("#ff3300"))
                if (gelirGider.harcama_tipi_id != null){
                    val harcamaTipi =  gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiGetirId(gelirGider.harcama_tipi_id!!)
                    if (harcamaTipi != null){
                        it.textViewHarcamaTipiAdi.text = harcamaTipi.ad
                        val imgUri =
                            Uri.parse("android.resource://com.bilgehankalay.gelirgidertakibi/drawable/" + harcamaTipi.drawable_name)
                        it.imageViewHarcamaTipiIco.setImageURI(imgUri)
                    }
                }
                else{
                    it.imageViewHarcamaTipiIco.setImageResource(R.drawable.error)
                }





            } //GİDER

            // ORTAK
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
        toplamGider = 0.0
        toplamGelir = 0.0
        gelirGiderList.forEach {
            if (it.tip == 0)
                toplamGelir += it.miktar
            else
                toplamGider += it.miktar
        }
        return Pair(toplamGelir,toplamGider)
    }

    fun gelirGiderListGuncelle(guncelGelirGiderList : ArrayList<GelirGider>){
        gelirGiderList = guncelGelirGiderList
        notifyDataSetChanged()
    }

}