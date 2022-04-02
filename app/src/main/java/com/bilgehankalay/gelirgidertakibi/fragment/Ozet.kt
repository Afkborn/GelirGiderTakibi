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
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.adapter.GelirGiderRW
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentOzetBinding
import java.text.NumberFormat
import java.util.*


class Ozet : Fragment() {
    private lateinit var binding : FragmentOzetBinding

    var gelirGiderList  : ArrayList<GelirGider> = arrayListOf()
    var toplamGelir = 0.0
    var toplamGider = 0.0

    private lateinit var gelirGiderTakipDatabase : GelirGiderTakipDatabase
    private lateinit var gelirGiderRWAdapter : GelirGiderRW

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
        gelirGiderList.add(GelirGider(tip = 1, ad = "Market", miktar = 118.58, eklenme_zamani = 1647896660, harcama_tipi_id = 1))
        gelirGiderList.add(GelirGider(tip = 1, ad = "Kredi", miktar = 1158.00, eklenme_zamani = 1647550999, harcama_tipi_id = 6))
        gelirGiderList.add(GelirGider(tip = 1, ad = "Yol", miktar = 350.00, eklenme_zamani = 1647550999, harcama_tipi_id = 4))
        gelirGiderList.add(GelirGider(tip = 1, ad = "Kıyafet", miktar = 1000.00, eklenme_zamani = 1647550999, harcama_tipi_id = 3))
        gelirGiderList.add(GelirGider(tip = 1, ad = "Ziynet", miktar = 1450.00, eklenme_zamani = 1647550999, harcama_tipi_id = 5))
        gelirGiderList.add(GelirGider(tip = 1, ad = "Yemek", miktar = 550.00, eklenme_zamani = 1647550999, harcama_tipi_id = 2))
        */
        yukleGelirGiderList()


        gelirGiderRWAdapter  = GelirGiderRW(gelirGiderList)
        recyclerViewSetSwap()

        gelirGiderRWAdapter!!.onItemClick = ::secilenGelirGiderClick // High Order Function



        ozetBilgisiYaz()

        binding.recyclerViewGelirGider.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.recyclerViewGelirGider.adapter = gelirGiderRWAdapter
        binding.recyclerViewGelirGider.setHasFixedSize(true)
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
                val kaydirilanGelirGider = gelirGiderList[viewHolder.adapterPosition]
                gelirGiderSilWithDialog(kaydirilanGelirGider)

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


}