package com.bilgehankalay.gelirgidertakibi.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.KategoriCardTasarimBinding

class KategorilerRW (private var harcamaTipleriList : ArrayList<HarcamaTipi>, var raporMu : Boolean) : RecyclerView.Adapter<KategorilerRW.HarcamaTipiCardTasarım>() {
    private lateinit var context : Context
    private lateinit var gelirGiderTakipDatabase : GelirGiderTakipDatabase

    var toplamHarcamaMiktar = 0.0

    var gelirStr = ""
    class HarcamaTipiCardTasarım(val harcamaTipiCardTasarim : KategoriCardTasarimBinding) : RecyclerView.ViewHolder(harcamaTipiCardTasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HarcamaTipiCardTasarım {
        context = parent.context
        gelirStr = context.getString(R.string.gelir)
        if (raporMu){
            setYuzde()
        }
        val layoutInflater = LayoutInflater.from(context)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(context)!!
        val harcamaTipiCardTasarim = KategoriCardTasarimBinding.inflate(layoutInflater,parent,false)
        return HarcamaTipiCardTasarım(harcamaTipiCardTasarim)

    }

    private fun setYuzde() {
        harcamaTipleriList.forEach {
            if (it.kategoriToplamHarcamaMiktar != null){
                toplamHarcamaMiktar += it.kategoriToplamHarcamaMiktar!!
            }
        }
    }



    override fun onBindViewHolder(holder: HarcamaTipiCardTasarım, position: Int) {
        val harcamaTipi = harcamaTipleriList[position]

        holder.harcamaTipiCardTasarim.let {
            // LOAD UI
            it.textViewKategoriAdi.text = harcamaTipi.ad
            if (raporMu){
                it.progressBarRaporYuzde.visibility = View.VISIBLE
                it.textViewRaporYuzde.visibility = View.VISIBLE
                it.imageViewLockOrDelete.visibility  = View.INVISIBLE
            }
            else{
                it.progressBarRaporYuzde.visibility = View.INVISIBLE
                it.textViewRaporYuzde.visibility = View.INVISIBLE
                it.imageViewLockOrDelete.visibility  = View.VISIBLE
            }
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
                        it.imageViewThumbnailKategori.textSize = 70F
                        it.imageViewThumbnailKategori.setText(kisaltma)
                    }
                    else if (itList.size > 1){
                        val kisaltma = "${harcamaAdi.get(0)}${harcamaAdi.get(1)}"
                        it.imageViewThumbnailKategori.textSize = 63F
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

        if (!raporMu){
            holder.harcamaTipiCardTasarim.let {
                if (harcamaTipi.is_custom){
                    it.textViewKategoriAdi.setOnClickListener {
                        showDialogGetText(context,harcamaTipi)
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
                        gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiSil(harcamaTipi)
                        harcamaTipleriList.remove(harcamaTipi)
                        notifyDataSetChanged()
                    }
                }
            }
        }
        else{
            val harcamaYuzde = harcamaTipi.kategoriToplamHarcamaMiktar?.div((toplamHarcamaMiktar / 100 ))
            val yuvarlananSayi = String.format("%.2f", harcamaYuzde)
            holder.harcamaTipiCardTasarim.progressBarRaporYuzde.max = toplamHarcamaMiktar.toInt()
            holder.harcamaTipiCardTasarim.progressBarRaporYuzde.progress = harcamaTipi.kategoriToplamHarcamaMiktar!!.toInt()
            holder.harcamaTipiCardTasarim.textViewRaporYuzde.text  = context.getString(R.string.harcama_yuzde,yuvarlananSayi)
        }



    }

    fun showDialogGetText(context:Context, harcamaTipi : HarcamaTipi){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.isim_degistir))
        val input = EditText(context)
        input.setHint(context.getString(R.string.yeni_isim))
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton(context.getString(R.string.tamam), DialogInterface.OnClickListener { dialog, which ->
            val m_Text = input.text.toString()
            if (!m_Text.isNullOrEmpty()){
                harcamaTipi.ad = m_Text
                gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiGuncelle(harcamaTipi)
                notifyDataSetChanged()
            }

        })
        builder.setNegativeButton(context.getString(R.string.iptal), DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.show()
    }



    override fun getItemCount(): Int {
        return harcamaTipleriList.size
    }

}