package com.bilgehankalay.gelirgidertakibi.adapter


import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.GelirGiderCardTasarimBinding
import java.io.File
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

            val lang = rw_parent!!.context.getString(R.string.lang)
            val country = rw_parent!!.context.getString(R.string.country)
            val formatlananMiktar = NumberFormat.getCurrencyInstance(Locale(lang,country)).format(gelirGider.miktar)
            //GELİR
            if (gelirGider.tip == 0){
                it.imageViewThumbnail.visibility = View.INVISIBLE
                it.textViewHarcamaTipiAdi.visibility = View.GONE
                it.imageViewGelirGiderIco.visibility = View.INVISIBLE
                it.imageViewHarcamaTipiIco.setImageResource(R.drawable.dollar)
                it.textViewHarcamaMiktar.text = rw_parent!!.context.getString(R.string.arti_bakiye,formatlananMiktar) // Resources.getSystem().getString(R.string.arti_bakiye,"1")
                it.progressBarHarcamaYuzde.max = toplamGelir.toInt()
                val harcamaYuzde =  gelirGider.miktar / (toplamGelir / 100 )
                val yuvarlananSayi = String.format("%.2f", harcamaYuzde)
                it.textViewHarcamaYuzde.text  = rw_parent!!.context.getString(R.string.harcama_yuzde,yuvarlananSayi)
                it.textViewHarcamaMiktar.setTextColor(Color.parseColor("#00cc00"))
                it.progressBarHarcamaYuzde.progress = gelirGider.miktar.toInt()
                it.textViewHarcamaAdi.text = gelirGider.ad

            }
            else if (gelirGider.tip == 1){
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

                if (gelirGider.harcama_tipi_id != null) {
                    setHarcamaTipi(it, gelirGider.harcama_tipi_id!!)
                }
                else{
                    setImageError(it)
                }
                it.progressBarHarcamaYuzde.progress = gelirGider.miktar.toInt()
                it.textViewHarcamaAdi.text = gelirGider.ad

            }

            //long to date
            val sdf = SimpleDateFormat("dd/MM/yy")
            val date = Date(gelirGider.eklenme_zamani )
            it.textViewHarcamaTarih.text = sdf.format(date)

            if (gelirGider.yinelenen_mi == true){
                it.imageViewGelirGiderIco.visibility = View.VISIBLE
                it.imageViewGelirGiderIco.setImageResource(R.drawable.repeat)
            }


        }
    }

    private fun setHarcamaTipi(gelirGiderTasarimBinding : GelirGiderCardTasarimBinding, harcama_tipi_id : Int){
        val harcamaTipi =  gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiGetirId(harcama_tipi_id)
        if (harcamaTipi != null){
            gelirGiderTasarimBinding.textViewHarcamaTipiAdi.text = harcamaTipi.ad
            if (harcamaTipi.has_drawable){
                gelirGiderTasarimBinding.imageViewThumbnail.visibility = View.INVISIBLE
                gelirGiderTasarimBinding.imageViewHarcamaTipiIco.visibility = View.VISIBLE
                if (!harcamaTipi.is_custom){
                    //drawable var temel harcama tipi
                    val imgUri =
                        Uri.parse("android.resource://com.bilgehankalay.gelirgidertakibi/drawable/" + harcamaTipi.drawable_name)
                    gelirGiderTasarimBinding.imageViewHarcamaTipiIco.setImageURI(imgUri)
                }
                else{
                    val uri : Uri = Uri.parse(harcamaTipi.drawable_name)
                    gelirGiderTasarimBinding.imageViewHarcamaTipiIco.setImageURI(uri)
                }

            }
            else{
                gelirGiderTasarimBinding.imageViewThumbnail.visibility = View.VISIBLE
                gelirGiderTasarimBinding.imageViewHarcamaTipiIco.visibility = View.INVISIBLE
                val harcamaAdi = harcamaTipi.ad
                harcamaAdi.split(" ").let {
                    if (it.size == 1){
                        //get first char
                        val kisaltma = harcamaAdi.get(0).toString()
                        gelirGiderTasarimBinding.imageViewThumbnail.setText(kisaltma)
                    }
                    else if (it.size > 1){
                        // get 1 2
                        val kisaltma = "${harcamaAdi.get(0)}${harcamaAdi.get(1)}"
                        gelirGiderTasarimBinding.imageViewThumbnail.setText(kisaltma)
                    }
                    else{
                        gelirGiderTasarimBinding.imageViewThumbnail.visibility = View.INVISIBLE
                        gelirGiderTasarimBinding.imageViewHarcamaTipiIco.visibility = View.VISIBLE
                        setImageError(gelirGiderTasarimBinding)
                    }
                }
            }
        }
        else{
            setImageError(gelirGiderTasarimBinding)
        }
    }
    private fun setImageError(gelirGiderTasarimBinding : GelirGiderCardTasarimBinding){
        gelirGiderTasarimBinding.imageViewHarcamaTipiIco.setImageResource(R.drawable.error)
    }

    override fun getItemCount(): Int {
        return gelirGiderList.size
    }

    fun yuzdeHesapla() : Pair<Double,Double>{
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