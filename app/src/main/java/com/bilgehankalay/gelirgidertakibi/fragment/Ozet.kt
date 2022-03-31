package com.bilgehankalay.gelirgidertakibi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.adapter.GelirGiderRW
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentOzetBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class Ozet : Fragment() {
    private lateinit var binding : FragmentOzetBinding

    var gelirGiderList  : ArrayList<GelirGider> = arrayListOf()
    var toplamGelir = 0.0
    var toplamGider = 0.0

    private lateinit var gelirGiderTakipDatabase : GelirGiderTakipDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(requireContext())!!


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOzetBinding.inflate(inflater,container,false)
        return binding.root
        
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bilgilerTemizle()

        /*
        gelirGiderList.add(GelirGider(tip =  0, ad = "Maaş", miktar = 6648.89, eklenme_zamani = 1647291660))
        gelirGiderList.add(GelirGider(tip = 1, ad = "Market", miktar = 118.58, eklenme_zamani = 1647896660))
        gelirGiderList.add(GelirGider(tip = 1, ad = "Kredi", miktar = 1158.00, eklenme_zamani = 1647550999))

         */





        val gelirGiderRWAdapter  = GelirGiderRW(gelirGiderList)

        val (pairToplamGelir, pairtoplamGider) = gelirGiderRWAdapter.toplamGelirGiderHesapla()
        toplamGelir = pairToplamGelir
        toplamGider = pairtoplamGider
        ozetBilgisiYaz()
        binding.recyclerViewGelirGider.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewGelirGider.adapter = gelirGiderRWAdapter
        binding.recyclerViewGelirGider.setHasFixedSize(true)
    }


    private fun ozetBilgisiYaz(){

        val formatlananGelir = NumberFormat.getCurrencyInstance(Locale("tr","TR")).format(toplamGelir)
        val formatlananGider = NumberFormat.getCurrencyInstance(Locale("tr","TR")).format(toplamGider)
        val formatlananNet = NumberFormat.getCurrencyInstance(Locale("tr","TR")).format(toplamGelir - toplamGider)
        binding.textViewGelir.text = formatlananGelir
        binding.textViewGider.text = formatlananGider
        binding.textViewNet.text = formatlananNet


    }

    private  fun bilgilerTemizle(){
        toplamGider = 0.0
        toplamGelir = 0.0
        gelirGiderList.clear()
    }


}