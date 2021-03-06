package com.yasaryasar.gelirgidertakibi.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yasaryasar.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.yasaryasar.gelirgidertakibi.Dialog.SearchDialogFragment
import com.yasaryasar.gelirgidertakibi.Model.GelirGider
import com.yasaryasar.gelirgidertakibi.Model.HarcamaTipi
import com.yasaryasar.gelirgidertakibi.R
import com.yasaryasar.gelirgidertakibi.adapter.GelirGiderRW
import com.yasaryasar.gelirgidertakibi.databinding.FragmentOzetBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class Ozet : Fragment(), SearchDialogFragment.SeciliItemListener {
    private lateinit var binding : FragmentOzetBinding

    var gelirGiderList  : ArrayList<GelirGider> = arrayListOf()
    var filtreliGelirGiderList  : ArrayList<GelirGider> = arrayListOf()
    var searchGelirGiderList : ArrayList<GelirGider> = arrayListOf()

    var yinelenenGelirGiderList : ArrayList<GelirGider> = arrayListOf()

    var toplamGelir = 0.0
    var toplamGider = 0.0

    private lateinit var gelirGiderTakipDatabase : GelirGiderTakipDatabase
    private lateinit var gelirGiderRWAdapter : GelirGiderRW

    var harcamaTipiAdListesi : ArrayList<String> = arrayListOf()
    var harcamaTipleri : ArrayList<HarcamaTipi> = arrayListOf()

    var filtreVarMi : Boolean = false

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
        yukleGelirGiderList()
        harcamaTipleriGetir()
        recyclerViewSetSwap()
        kontrolYinelenenGelirGiderList()

        gelirGiderRWAdapter  = GelirGiderRW(gelirGiderList)
        gelirGiderRWAdapter!!.onItemClick = ::secilenGelirGiderClick // High Order Function
        ozetBilgisiYaz()


        // RECYCLER V??EW
        binding.recyclerViewGelirGider.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewGelirGider.adapter = gelirGiderRWAdapter
        binding.recyclerViewGelirGider.setHasFixedSize(true)


        //F??LTRELE
        binding.buttonFiltre.setOnClickListener {
            if (!filtreVarMi){
                SearchDialogFragment(harcamaTipiAdListesi,this).show(childFragmentManager,"ara")
            }
            else{
                gelirGiderRWAdapter.gelirGiderListGuncelle(gelirGiderList)
                filtreVarMi = !filtreVarMi
                binding.buttonFiltre.setText(requireContext().getString(R.string.filtrele))
            }
        }

        //ARAMA
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    aramaYap(p0)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null) {
                    aramaYap(p0)
                }
                return false
            }
        })
    }

    private fun aramaYap(p0 : String) {
        val p0Lower = p0.lowercase()
        searchGelirGiderList.clear()
        if (filtreVarMi){
            filtreliGelirGiderList.forEach {
                if (it.ad != null){
                    val kucukAd = it.ad!!.lowercase()
                    if (kucukAd.startsWith(p0Lower)){
                        searchGelirGiderList.add(it)
                    }
                }
            }
        }
        else{
            gelirGiderList.forEach {
                if (it.ad != null){
                    val kucukAd = it.ad!!.lowercase()
                    if (kucukAd.startsWith(p0Lower)){
                        searchGelirGiderList.add(it)
                    }
                }
            }
        }
        gelirGiderRWAdapter.gelirGiderListGuncelle(searchGelirGiderList)

    }

    private fun ozetBilgisiYaz(){

        val (pairToplamGelir, pairtoplamGider) = gelirGiderRWAdapter.yuzdeHesapla()
        toplamGelir = pairToplamGelir
        toplamGider = pairtoplamGider

        val lang = requireContext().getString(R.string.lang)
        val country = requireContext().getString(R.string.country)

        val formatlananGelir = NumberFormat.getCurrencyInstance(Locale(lang,country)).format(toplamGelir)
        val formatlananGider = NumberFormat.getCurrencyInstance(Locale(lang,country)).format(toplamGider)
        val formatlananNet = NumberFormat.getCurrencyInstance(Locale(lang,country)).format(toplamGelir - toplamGider)
        binding.textViewGelir.text = formatlananGelir
        binding.textViewGider.text = formatlananGider
        binding.textViewNet.text = formatlananNet
    }

    private  fun bilgilerTemizle(){
        toplamGider = 0.0
        toplamGelir = 0.0
        gelirGiderList.clear()
        harcamaTipiAdListesi.clear()
        harcamaTipleri.clear()
        filtreliGelirGiderList.clear()
        yinelenenGelirGiderList.clear()
        searchGelirGiderList.clear()
    }

    private fun secilenGelirGiderClick(gelenGelirGider: GelirGider){
        val gecisAction = OzetDirections.ozetToGelirGiderDetay(gelenGelirGider)
        findNavController().navigate(gecisAction)

    }

    private fun yukleGelirGiderList(){
        gelirGiderTakipDatabase.gelirGiderDAO().tumGelirGider().forEach {
            if (it != null){
                if (it.tip == 3){
                    yinelenenGelirGiderList.add(it)
                }
                else{
                    gelirGiderList.add(it)
                }

            }
        }
    }

    private fun kontrolYinelenenGelirGiderList(){
        yinelenenGelirGiderList.forEach {
                if (System.currentTimeMillis() >= it.eklenme_zamani && it.eklenmis_mi == false){
                    val anaGelirGider = gelirGiderTakipDatabase.gelirGiderDAO().idGelirGider(it.ana_harcama!!)
                    if (anaGelirGider != null){
                        anaGelirGider!!.eklenme_zamani = it.eklenme_zamani
                        it.eklenmis_mi = true
                        val eklenecekGelirGider = GelirGider(
                            tip = anaGelirGider.tip,
                            ad = anaGelirGider.ad,
                            miktar = anaGelirGider.miktar,
                            aciklama = anaGelirGider.aciklama,
                            eklenme_zamani = it.eklenme_zamani,
                            duzenli_mi = anaGelirGider.duzenli_mi,
                            tekrar_tipi = anaGelirGider.tekrar_tipi,
                            yinelenen_mi = true
                        )
                        if (anaGelirGider.tip == 0){
                            eklenecekGelirGider.aktif_pasif = anaGelirGider.aktif_pasif
                        }
                        else if (anaGelirGider.tip == 1){
                            eklenecekGelirGider.harcama_tipi_id = anaGelirGider.harcama_tipi_id
                        }

                        gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderEkle(eklenecekGelirGider)
                        gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderGuncelle(it)

                        gelirGiderList.add(0,eklenecekGelirGider)
                    }
                    else{
                        // Yinelenen harcamas?? var fakat ana harcamas?? yok. yinelenen haqrcamas?? sil.
                        Log.e("Yinelenen Gelir Gider", it.toString())
                        Log.e("Ana Gelir Gider",anaGelirGider.toString())
                        gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderSil(it)

                    }
                    }
                }
    }

    private fun recyclerViewSetSwap(){
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (filtreVarMi){
                    val kaydirilanGelirGider = filtreliGelirGiderList[viewHolder.adapterPosition]
                    gelirGiderSilWithDialog(kaydirilanGelirGider)
                }
                else{
                    val kaydirilanGelirGider = gelirGiderList[viewHolder.adapterPosition]
                    gelirGiderSilWithDialog(kaydirilanGelirGider)
                }
            }

        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallBack)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewGelirGider)
    }

    private fun gelirGiderSil(silinecekGelirGider : GelirGider){
        gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderSil(silinecekGelirGider)
        gelirGiderList.remove(silinecekGelirGider)
        ozetBilgisiYaz()
    }


    private fun gelirGiderSilWithDialog(silinecekGelirGider: GelirGider){
        lateinit var dialog:AlertDialog
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(requireContext().getString(R.string.sil_tite))
        builder.setMessage(requireContext().getString(R.string.sil_message,silinecekGelirGider.ad))

        val dialogClickListener = DialogInterface.OnClickListener{_,which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    gelirGiderSil(silinecekGelirGider)
                    gelirGiderRWAdapter.notifyDataSetChanged()

                    bilgilerTemizle()
                    yukleGelirGiderList()
                    harcamaTipleriGetir()

                    gelirGiderRWAdapter.gelirGiderListGuncelle(gelirGiderList)
                    ozetBilgisiYaz()
                    if (filtreVarMi){
                        filtreVarMi = !filtreVarMi
                    }
                    binding.buttonFiltre.setText(requireContext().getString(R.string.filtrele))
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    gelirGiderRWAdapter.notifyDataSetChanged()
                }
            }
        }

        builder.setPositiveButton(requireContext().getString(R.string.evet),dialogClickListener)
        builder.setNegativeButton(requireContext().getString(R.string.hayir),dialogClickListener)

        dialog = builder.create()
        dialog.show()
    }

    private fun harcamaTipleriGetir(){
        harcamaTipleri.clear()
        harcamaTipiAdListesi.clear()
        gelirGiderTakipDatabase.harcamaTipiDAO().tumHarcamaTipi().forEach {
            if (it != null){
                harcamaTipleri.add(it)
                harcamaTipiAdListesi.add(it.ad)
            }
        }
    }

    override fun seciliFiltre(seciliFiltreler: ArrayList<String>) {
        filtreliGelirGiderList.clear()
        filtreVarMi = seciliFiltreler.size > 0
        if (filtreVarMi){
            binding.buttonFiltre.setText(requireContext().getString(R.string.filtre_kald??r))
        }
        val filtrelenecekHarcamaTipleri : ArrayList<HarcamaTipi> = arrayListOf()
        seciliFiltreler.forEach {
            //D??ZENL?? ????LEM
            if (it == requireContext().getString(R.string.duzenli_islem)){
                 gelirGiderTakipDatabase.gelirGiderDAO().duzenliGelirGider().forEach{ gelirGider ->
                     if (gelirGider != null)
                        filtreliGelirGiderList.add(gelirGider)
                 }
            }
            // GEL??RLER??M
            else if (it == requireContext().getString(R.string.gelirlerim)){
                gelirGiderTakipDatabase.gelirGiderDAO().gelirlerim().forEach { gelirim ->
                    if (gelirim != null)
                        filtreliGelirGiderList.add(gelirim)
                }
            }
            //G??DERLER??M
            else if (it == requireContext().getString(R.string.giderlerim)){
                gelirGiderTakipDatabase.gelirGiderDAO().giderlerim().forEach { giderim ->
                    if (giderim !=null)
                        filtreliGelirGiderList.add(giderim)
                }
            }
            // HARCAMA T??PLER??
            else{
                val databaseReturnHarcamaTipi = gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiGetirAd(it)
                if (databaseReturnHarcamaTipi != null){
                    filtrelenecekHarcamaTipleri.add(databaseReturnHarcamaTipi)
                }
            }
        }
        filtrelenecekHarcamaTipleri.forEach {
            gelirGiderTakipDatabase.gelirGiderDAO().harcamaTipiGelirGider(it.id).forEach { gelirGider ->
                if (gelirGider != null)
                    filtreliGelirGiderList.add(gelirGider)
            }
        }
        gelirGiderRWAdapter.gelirGiderListGuncelle(filtreliGelirGiderList)
    }

}