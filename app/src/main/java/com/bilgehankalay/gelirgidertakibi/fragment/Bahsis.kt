package com.bilgehankalay.gelirgidertakibi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentBahsisBinding



class Bahsis : Fragment() {

    private var hesapMiktar : Double = 0.0
    private var kisiSayisi : Int = 2
    private var bahsisYuzdesi : Double = 10.0
    private var bahsis : Double = 0.0
    private var bahsisYuzdeSeekBar : Int = 2
    private var toplamHesap : Double = 0.0
    private var kisiBasiMiktar : Double = 0.0

    private lateinit var gelirGiderTakipDatabase: GelirGiderTakipDatabase
    private lateinit var binding : FragmentBahsisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(requireContext())!!

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBahsisBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editTextKisiSayisi.setText("2")
        binding.editTextHesapMiktar.addTextChangedListener {
            if (binding.editTextHesapMiktar.text.isNotEmpty()){
                hesapMiktar = it.toString().toDouble()
                hesapla()
            }

        }
        binding.fabArti.setOnClickListener {
            val guncelDeger = binding.editTextKisiSayisi.text.toString().toInt()
            if (guncelDeger < 20){
                binding.editTextKisiSayisi.setText((guncelDeger + 1).toString())
                kisiSayisi++
                hesapla()
            }

        }
        binding.fabEksi.setOnClickListener {
            val guncelDeger = binding.editTextKisiSayisi.text.toString().toInt()
            if (guncelDeger > 1){
                binding.editTextKisiSayisi.setText((guncelDeger - 1).toString())
                kisiSayisi--
                hesapla()
            }

        }

        binding.seekBarBahsisYuzde.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                bahsisYuzdeSeekBar = p1
                hesapla()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.buttonEkle.setOnClickListener {
            val eklenme = System.currentTimeMillis()
            val harcamaTipi = gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiGetirAd(requireContext().getString(R.string.yemek))
            if (harcamaTipi != null){
                val aciklama =  requireContext().getString(R.string.bahsis_aciklama,kisiSayisi.toString())
                val harcama = GelirGider(tip = 1, ad = "Hesap", miktar = kisiBasiMiktar, aciklama = aciklama, duzenli_mi = false, eklenme_zamani = eklenme, harcama_tipi_id = harcamaTipi.id)
                gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderEkle(harcama)
                goOzet()
            }

        }

    }

    private fun goOzet(){
        val gecisAction = BahsisDirections.bahsisToOzet()
        findNavController().navigate(gecisAction)
    }

    private fun hesapla(){
        bahsisYuzdesi = bahsisYuzdeSeekBar * 5.0
        if (hesapMiktar!= 0.0){
                bahsis = (hesapMiktar / 100) * bahsisYuzdesi
                binding.editTextBahsis.setText(bahsis.toString())
                toplamHesap = hesapMiktar + bahsis
                kisiBasiMiktar = toplamHesap / kisiSayisi
                binding.textViewHesapMiktar.setText(requireContext().getString(R.string.hesap_miktar_toplam,hesapMiktar.toString()))
                binding.textViewToplam.setText(requireContext().getString(R.string.toplam_hesap,toplamHesap.toString()))
                binding.textViewBahsisMiktar.setText(requireContext().getString(R.string.bahsis_miktar,bahsis.toString()))
                binding.textViewKisiBasiMiktar.setText(requireContext().getString(R.string.kisi_basi_miktar,kisiBasiMiktar.toString()))
        }
    }


}