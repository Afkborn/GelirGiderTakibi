package com.bilgehankalay.gelirgidertakibi.fragment

import android.graphics.Color
import android.os.Bundle
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
import com.bilgehankalay.gelirgidertakibi.adapter.LegendRW
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentRaporBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import java.text.SimpleDateFormat
import java.util.*


class Rapor : Fragment() {
    private lateinit var binding : FragmentRaporBinding
    private lateinit var gelirGiderTakipDatabase: GelirGiderTakipDatabase

    private var tarihlerListe : List<Long?> = arrayListOf()
    private var tarihlerAdListesi  : ArrayList<String> = arrayListOf()

    private var seciliTarih : String? = null


    private var harcamalar : ArrayList<GelirGider> = arrayListOf()
    private var kategoriler : ArrayList<HarcamaTipi> = arrayListOf()
    var startOfMonth = 0L
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
        setChart()


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

        startOfMonth = cal.timeInMillis
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
        loadChart()

    }

    private fun setChart(){
        binding.let {

            //hollow pie chart
            it.pieChart.isDrawHoleEnabled = false
            it.pieChart.setTouchEnabled(false)

            it.pieChart.setDrawEntryLabels(false)

            //adding padding
            it.pieChart.setUsePercentValues(true)
            it.pieChart.isRotationEnabled = false

            it.pieChart.legend.isEnabled = false
            /*
            it.pieChart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
            it.pieChart.legend.isWordWrapEnabled = false

             */
        }

    }
    private fun loadChart(){

        var toplamHarcama = 0.0

        val dataEntries = ArrayList<PieEntry>()
        val colors: ArrayList<Int> = ArrayList()
        if (kategoriler.size == 0){
            dataEntries.add(PieEntry(100.0f,""))
            val rnd = Random()
            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            colors.add(color)
        }

        kategoriler.forEach {
            if (it.kategoriToplamHarcamaMiktar!= null){
                toplamHarcama += it.kategoriToplamHarcamaMiktar!!
                dataEntries.add(PieEntry(it.kategoriToplamHarcamaMiktar!!.toFloat(),it.ad))
            }
            val rnd = Random()
            val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            colors.add(color)
            it.chartColor = color
        }
        kategoriler.forEach {
            it.yuzde = it.kategoriToplamHarcamaMiktar!! / (toplamHarcama / 100)
        }


        binding.let {
            val month_date = SimpleDateFormat("MMMM YYYY")
            val month_name = month_date.format(startOfMonth)

            it.pieChart.description.text = ""
            binding.pieChart.centerText = month_name
            binding.pieChart.setCenterTextSize(20.0f)
            it.pieChart.description.textSize = 0.0f
        }


        val dataSet = PieDataSet(dataEntries, "")
        val data = PieData(dataSet)

        // In Percentage
        data.setValueFormatter(PercentFormatter())
        dataSet.sliceSpace = 5f
        dataSet.colors = colors

        binding.pieChart.data = data
        data.setValueTextSize(20f)
        binding.pieChart.setExtraOffsets(0f, 0f, 0f, 0f)
        binding.pieChart.animateY(1000, Easing.EaseInOutQuad)

        binding.pieChart.holeRadius = 60f
        binding.pieChart.transparentCircleRadius = 61f
        binding.pieChart.isDrawHoleEnabled = true
        binding.pieChart.setHoleColor(Color.WHITE)
        setRecyclerViewAdapter()
    }

    private fun setRecyclerViewAdapter() {
        binding.let {
            val legendAdapter = LegendRW(kategoriler)
            it.recyclerViewLegendHorizontal.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            it.recyclerViewLegendHorizontal.adapter = legendAdapter
            it.recyclerViewLegendHorizontal.setHasFixedSize(true)
        }
    }





}
