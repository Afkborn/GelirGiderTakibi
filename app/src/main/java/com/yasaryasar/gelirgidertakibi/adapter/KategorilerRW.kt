package com.yasaryasar.gelirgidertakibi.adapter

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
import com.yasaryasar.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.yasaryasar.gelirgidertakibi.Model.HarcamaTipi
import com.yasaryasar.gelirgidertakibi.R
import com.yasaryasar.gelirgidertakibi.databinding.KategoriCardTasarimBinding

class KategorilerRW (private var harcamaTipleriList : ArrayList<HarcamaTipi>, var raporMu : Boolean) : RecyclerView.Adapter<KategorilerRW.HarcamaTipiCardTasarım>() {
    private lateinit var context : Context
    private lateinit var gelirGiderTakipDatabase : GelirGiderTakipDatabase

    var toplamHarcamaMiktar = 0.0

    class HarcamaTipiCardTasarım(val harcamaTipiCardTasarim : KategoriCardTasarimBinding) : RecyclerView.ViewHolder(harcamaTipiCardTasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HarcamaTipiCardTasarım {
        context = parent.context


        val layoutInflater = LayoutInflater.from(context)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(context)!!
        val harcamaTipiCardTasarim = KategoriCardTasarimBinding.inflate(layoutInflater,parent,false)
        return HarcamaTipiCardTasarım(harcamaTipiCardTasarim)

    }





    override fun onBindViewHolder(holder: HarcamaTipiCardTasarım, position: Int) {
        val harcamaTipi = harcamaTipleriList[position]

        holder.harcamaTipiCardTasarim.let {
            // LOAD UI
            it.textViewKategoriAdi.text = harcamaTipi.ad
            if (raporMu){
                it.imageViewLockOrDelete.visibility  = View.INVISIBLE
            }
            else{
                it.imageViewLockOrDelete.visibility  = View.VISIBLE
            }
            if (harcamaTipi.has_drawable){
                it.imageViewThumbnailKategori.visibility = View.INVISIBLE
                it.imageViewKategoriIco.visibility = View.VISIBLE

                if (!harcamaTipi.is_custom){
                    it.imageViewLockOrDelete.setImageResource(R.drawable.lock)
                    val imgUri =
                        Uri.parse("android.resource://com.yasaryasar.gelirgidertakibi/drawable/" + harcamaTipi.drawable_name) //TODO
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

            holder.harcamaTipiCardTasarim.let {
                if (harcamaTipi.is_custom){
                    it.textViewKategoriAdi.setOnClickListener {
                        showDialogGetText(context,harcamaTipi)
                    }
                    if (harcamaTipi.has_drawable){
                        it.imageViewKategoriIco.setOnClickListener {
                            Log.e("LOG","resmi var güncelle")
                        }
                    }
                    else{
                        it.imageViewThumbnailKategori.setOnClickListener {
                            Log.e("LOG","resmi yok thumnbail")
                        }

                    }
                    it.imageViewLockOrDelete.setOnClickListener {
                        gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiSil(harcamaTipi)
                        harcamaTipleriList.remove(harcamaTipi)
                        notifyDataSetChanged()
                    }
                }
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