package com.bilgehankalay.gelirgidertakibi.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Dialog.SearchDialogFragment
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.adapter.GelirGiderRW
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentOzetBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class Ozet : Fragment(), SearchDialogFragment.SeciliItemListener {
    private lateinit var binding : FragmentOzetBinding

    var gelirGiderList  : ArrayList<GelirGider> = arrayListOf()
    var filtreliGelirGiderList  : ArrayList<GelirGider> = arrayListOf()

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

        gelirGiderRWAdapter  = GelirGiderRW(gelirGiderList)
        recyclerViewSetSwap()
        gelirGiderRWAdapter!!.onItemClick = ::secilenGelirGiderClick // High Order Function

        ozetBilgisiYaz()

        binding.recyclerViewGelirGider.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewGelirGider.adapter = gelirGiderRWAdapter
        binding.recyclerViewGelirGider.setHasFixedSize(true)


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
    }


    private fun ozetBilgisiYaz(){

        val (pairToplamGelir, pairtoplamGider) = gelirGiderRWAdapter.toplamGelirGiderHesapla()
        toplamGelir = pairToplamGelir
        toplamGider = pairtoplamGider

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
        harcamaTipiAdListesi.clear()
        harcamaTipleri.clear()
        filtreliGelirGiderList.clear()
    }

    private fun secilenGelirGiderClick(gelenGelirGider: GelirGider){
        if (gelenGelirGider != null){
            Log.e("Tıklanan Gelir Gider",gelenGelirGider.ad!!)
        }
    }
    private fun yukleGelirGiderList(){
        gelirGiderTakipDatabase.gelirGiderDAO().tumGelirGider().forEach {
            if (it != null){
                gelirGiderList.add(it)
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
                //kaydırıldığında çalışır
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
                    filtreVarMi = !filtreVarMi
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
        gelirGiderTakipDatabase.harcamaTipiDAO().tumHarcamaTipi().forEach {
            if (it != null){
                harcamaTipleri.add(it)
                harcamaTipiAdListesi.add(it.ad)

            }
        }
    }

    override fun seciliItem(seciliItem: ArrayList<String>) {
        filtreliGelirGiderList.clear()

        filtreVarMi = seciliItem.size > 0
        if (filtreVarMi){
            binding.buttonFiltre.setText(requireContext().getString(R.string.filtre_kaldır))
        }
        val filtrelenecekHarcamaTipleri : ArrayList<HarcamaTipi> = arrayListOf()
        seciliItem.forEach {
            if (it == requireContext().getString(R.string.duzenli_islem)){
                //TODO düzenli işlemleri listele
                 gelirGiderTakipDatabase.gelirGiderDAO().duzenliGelirGider().forEach{ gelirGider ->
                     if (gelirGider != null)
                        filtreliGelirGiderList.add(gelirGider)
                 }
            }
            else if (it == requireContext().getString(R.string.gelirlerim)){
                gelirGiderTakipDatabase.gelirGiderDAO().gelirlerim().forEach { gelirim ->
                    if (gelirim != null)
                        filtreliGelirGiderList.add(gelirim)
                }
            }
            else if (it == requireContext().getString(R.string.giderlerim)){
                gelirGiderTakipDatabase.gelirGiderDAO().giderlerim().forEach { giderim ->
                    if (giderim !=null)
                        filtreliGelirGiderList.add(giderim)
                }
            }
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