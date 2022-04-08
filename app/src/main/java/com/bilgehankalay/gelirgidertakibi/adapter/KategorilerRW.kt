package com.bilgehankalay.gelirgidertakibi.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.KategoriCardTasarimBinding

class KategorilerRW (private var harcamaTipleriList : ArrayList<HarcamaTipi>) : RecyclerView.Adapter<KategorilerRW.HarcamaTipiCardTasarım>() {

    class HarcamaTipiCardTasarım(val harcamaTipiCardTasarim : KategoriCardTasarimBinding) : RecyclerView.ViewHolder(harcamaTipiCardTasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HarcamaTipiCardTasarım {
        val layoutInflater = LayoutInflater.from(parent.context)
        val harcamaTipiCardTasarim = KategoriCardTasarimBinding.inflate(layoutInflater,parent,false)
        return HarcamaTipiCardTasarım(harcamaTipiCardTasarim)

    }

    override fun onBindViewHolder(holder: HarcamaTipiCardTasarım, position: Int) {
        val harcamaTipi = harcamaTipleriList[position]
        holder.harcamaTipiCardTasarim.let {
            if (harcamaTipi.is_custom){
                it.textViewKategoriAdi.setOnClickListener {
                    //TODO İSME TIKLANDI İSİM DEĞİŞTİR
                    Log.e("LOG","isim güncelle")
                }
                if (harcamaTipi.has_drawable){
                    it.imageViewKategoriIco.setOnClickListener {
                        // TODO RESİM VARDI TIKLANDI RESİM DEĞİŞTİR
                        Log.e("LOG","resmi var güncelle")
                    }
                }
                else{
                    it.imageViewThumbnailKategori.setOnClickListener {
                        // TODO RESMİ YOKTU THUMBNAİL TIKLANDI RESİM KOY
                        Log.e("LOG","resmi yok thumnbail")
                    }

                }

                it.imageViewLockOrDelete.setOnClickListener {
                    // TODO SİL
                }
            }

            // LOAD UI
            it.textViewKategoriAdi.text = harcamaTipi.ad
            if (harcamaTipi.has_drawable){
                it.imageViewThumbnailKategori.visibility = View.INVISIBLE
                it.imageViewKategoriIco.visibility = View.VISIBLE

                if (!harcamaTipi.is_custom){
                    it.imageViewLockOrDelete.setImageResource(R.drawable.lock)
                    val imgUri =
                        Uri.parse("android.resource://com.bilgehankalay.gelirgidertakibi/drawable/" + harcamaTipi.drawable_name)
                    it.imageViewKategoriIco.setImageURI(imgUri)
                }
                else{
                    it.imageViewLockOrDelete.setImageResource(R.drawable.delete)
                    val uri : Uri = Uri.parse(harcamaTipi.drawable_name)
                    it.imageViewKategoriIco.setImageURI(uri)
                }
            }
            else{
                it.imageViewThumbnailKategori.visibility = View.VISIBLE
                it.imageViewKategoriIco.visibility = View.INVISIBLE
                if (!harcamaTipi.is_custom){
                    it.imageViewLockOrDelete.setImageResource(R.drawable.lock)
                }
                else{
                    it.imageViewLockOrDelete.setImageResource(R.drawable.delete)
                }
                val harcamaAdi = harcamaTipi.ad
                harcamaAdi.split(" ").let { itList ->
                    if (itList.size == 1){
                        val kisaltma = harcamaAdi.get(0).toString()
                        it.imageViewThumbnailKategori.setText(kisaltma)
                    }
                    else if (itList.size > 1){
                        val kisaltma = "${harcamaAdi.get(0)}${harcamaAdi.get(1)}"
                        it.imageViewThumbnailKategori.setText(kisaltma)
                    }
                    else{
                        it.imageViewThumbnailKategori.visibility = View.INVISIBLE
                        it.imageViewKategoriIco.visibility = View.VISIBLE
                        it.imageViewKategoriIco.setImageResource(R.drawable.error)
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return harcamaTipleriList.size
    }

}