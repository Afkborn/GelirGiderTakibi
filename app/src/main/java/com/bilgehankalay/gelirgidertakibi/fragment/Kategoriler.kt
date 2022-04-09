package com.bilgehankalay.gelirgidertakibi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.adapter.KategorilerRW
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentKategorilerBinding


class Kategoriler : Fragment() {
    private lateinit var binding : FragmentKategorilerBinding
    private lateinit var gelirGiderTakipDatabase: GelirGiderTakipDatabase


    var harcamaTipleri : ArrayList<HarcamaTipi> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(requireContext())!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKategorilerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        harcamaTipleriGetir()
        val kategorilerRw = KategorilerRW(harcamaTipleri)
        binding.recyclerViewKategoriler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewKategoriler.adapter = kategorilerRw
        binding.recyclerViewKategoriler.setHasFixedSize(true)

        binding.buttonEkle.setOnClickListener {
            goKategoriEkle()
        }
    }
    private fun harcamaTipleriGetir(){
        harcamaTipleri.clear()
        gelirGiderTakipDatabase.harcamaTipiDAO().tumHarcamaTipi().forEach {
            if (it != null){
                harcamaTipleri.add(it)
            }
        }
    }
    private fun goKategoriEkle(){
        val gecisAction = KategorilerDirections.kategorilerToKategoriEkle(gelinenEkran = 1)
        findNavController().navigate(gecisAction)
    }

}