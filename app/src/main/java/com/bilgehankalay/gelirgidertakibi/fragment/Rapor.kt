package com.bilgehankalay.gelirgidertakibi.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.adapter.GelirGiderRW
import com.bilgehankalay.gelirgidertakibi.adapter.KategorilerRW
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentRaporBinding
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*


class Rapor : Fragment() {
    private lateinit var binding : FragmentRaporBinding
    private lateinit var gelirGiderTakipDatabase: GelirGiderTakipDatabase

    private var tarihlerListe : List<Long?> = arrayListOf()
    private var tarihlerAdListesi  : ArrayList<String> = arrayListOf()

    private var seciliTarih : String? = null


    private lateinit var gelirGiderRaporRWAdapter : GelirGiderRW
    private lateinit var kategorilerRaporRWAdapter : KategorilerRW


    private var harcamalar : ArrayList<GelirGider> = arrayListOf()
    private var kategoriler : ArrayList<HarcamaTipi> = arrayListOf()

    var toplamGelirMiktar = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(requireContext())!!


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRaporBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSpinnerListener()
        spinnerYukle()
        binding.recyclerViewGelirGiderRapor.visibility = View.INVISIBLE
        binding.recyclerViewKategoriRapor.visibility = View.VISIBLE
        loadTabLayoutListener()

    }



    private fun tarihlerYukle(){
        tarihlerAdListesi.clear()
        tarihlerListe = gelirGiderTakipDatabase.gelirGiderDAO().tumGelirGiderTarih()
        val yearDate = SimpleDateFormat("MM/yyyy")

        tarihlerListe.forEach {
            if (it != null) {
                val monthYear = yearDate.format(Date(it))
                if (!tarihlerAdListesi.contains(monthYear)){
                    tarihlerAdListesi.add(monthYear)
                }
            }
        }


    }

    private fun spinnerYukle(){
        tarihlerYukle()
        val adapter: ArrayAdapter<*>
        adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            tarihlerAdListesi
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRaporTarihAraligi.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }
    private fun setSpinnerListener(){
        binding.spinnerRaporTarihAraligi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                seciliTarih = tarihlerAdListesi[p2]
                getHarcama()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                seciliTarih = null
            }

        }

    }
    private fun dateToUnix(seciliTarih : String) : Pair<Long,Long>{
        val month = seciliTarih.split("/")[0].toInt()
        val year = seciliTarih.split("/")[1].toInt()
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MONTH] = month -1
        cal[Calendar.YEAR] = year
        cal.clear(Calendar.MINUTE)
        cal.clear(Calendar.SECOND)
        cal.clear(Calendar.MILLISECOND)
        cal[Calendar.DAY_OF_MONTH] = 1

        val startOfMonth = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val finishOfMonth = cal.timeInMillis
        return Pair(startOfMonth,finishOfMonth)
    }

    private fun getHarcama(){
        harcamalar.clear()
        kategoriler.clear()
        val (pairStart, pairStop) = dateToUnix(seciliTarih!!)

        gelirGiderTakipDatabase.gelirGiderDAO().tumGelirGiderAy(pairStart,pairStop).forEach {
            if (it != null){
                harcamalar.add(it)
                if (it.tip == 0){
                    //Gelir
                    toplamGelirMiktar += it.miktar
                }
                else if (it.tip == 1){
                    //Gider
                    if (it.harcama_tipi_id != null){
                        val kategoriHarcama = gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiGetirId(it.harcama_tipi_id!!)
                        if (kategoriHarcama != null){
                            if (!kategoriler.contains(kategoriHarcama)){
                                kategoriHarcama.kategoriToplamHarcamaMiktar = it.miktar
                                kategoriler.add(kategoriHarcama)
                            }
                            else{
                                val kategorilerId = kategoriler.indexOf(kategoriHarcama)
                                kategoriler[kategorilerId].kategoriToplamHarcamaMiktar = kategoriler[kategorilerId].kategoriToplamHarcamaMiktar?.plus(
                                    it.miktar
                                )
                            }

                        }
                    }
                }


            }
        }

        loadRecyclerViewGelirGider()
        loadRecyclerViewKategoriler()

    }

    private fun loadRecyclerViewGelirGider(){
        gelirGiderRaporRWAdapter = GelirGiderRW(harcamalar)
        gelirGiderRaporRWAdapter.yuzdeHesapla()
        binding.recyclerViewGelirGiderRapor.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewGelirGiderRapor.adapter = gelirGiderRaporRWAdapter
        binding.recyclerViewGelirGiderRapor.setHasFixedSize(true)
    }

    private fun loadRecyclerViewKategoriler(){
        kategorilerRaporRWAdapter = KategorilerRW(kategoriler,true)
        binding.recyclerViewKategoriRapor.layoutManager = LinearLayoutManager(requireContext(),
        LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewKategoriRapor.adapter = kategorilerRaporRWAdapter
        binding.recyclerViewKategoriRapor.setHasFixedSize(true)

    }

    private fun loadTabLayoutListener(){
        binding.raporTabLayout.addOnTabSelectedListener( object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null){
                    if (tab.position == 0){
                        // Kategori
                        binding.recyclerViewGelirGiderRapor.visibility = View.INVISIBLE
                        binding.recyclerViewKategoriRapor.visibility = View.VISIBLE
                    }
                    if (tab.position == 1){
                        // Gelir Gider
                        binding.recyclerViewGelirGiderRapor.visibility = View.VISIBLE
                        binding.recyclerViewKategoriRapor.visibility = View.INVISIBLE
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }






}