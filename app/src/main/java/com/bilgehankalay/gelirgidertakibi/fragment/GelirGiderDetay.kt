package com.bilgehankalay.gelirgidertakibi.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.core.view.marginTop
import androidx.navigation.fragment.navArgs
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentGelirGiderDetayBinding
import java.text.SimpleDateFormat
import java.util.*


class GelirGiderDetay : Fragment() {

    private lateinit var binding : FragmentGelirGiderDetayBinding
    private val args : GelirGiderDetayArgs by navArgs()
    private lateinit var gelenGelirGider : GelirGider
    private lateinit var gelirGiderTakipDatabase : GelirGiderTakipDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gelenGelirGider = args.gelenGelirGider
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(requireContext())!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGelirGiderDetayBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.also {
            if (gelenGelirGider.tip == 0){
                it.textViewGelirGider.text = requireContext().getString(R.string.gelir)
                it.textViewGelirHarcamaTipiText.text = requireContext().getString(R.string.gelir_tipi)
                if (gelenGelirGider.aktif_pasif  == true){
                    it.textViewGelirHarcamaTipi.text = requireContext().getString(R.string.aktif_gelir)
                }
                else{
                    it.textViewGelirHarcamaTipi.text = requireContext().getString(R.string.pasif_gelir)
                }


            }
            else{
                it.textViewGelirGider.text = requireContext().getString(R.string.gider)
                it.textViewGelirHarcamaTipiText.text = requireContext().getString(R.string.harcama_tipi)
                val harcamaTipi =  gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiGetirId(gelenGelirGider.harcama_tipi_id!!)
                it.textViewGelirHarcamaTipi.text = harcamaTipi!!.ad
            }
            it.editTextAd.setText(gelenGelirGider.ad)
            it.editTextMiktar.setText(gelenGelirGider.miktar.toString())
            it.editTextAciklama.setText(gelenGelirGider.aciklama.toString())

            it.textViewEklenmeZamani.text = convertLongToTime(gelenGelirGider.eklenme_zamani)

            if(gelenGelirGider.duzenli_mi == true){
                it.checkBoxDuzenliMi.isChecked = true
            }
            else{
                it.constraintLayoutTekrarSuresi.visibility = View.INVISIBLE
            }

        }

    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm dd/MM/yyyy")
        return format.format(date)
    }



}