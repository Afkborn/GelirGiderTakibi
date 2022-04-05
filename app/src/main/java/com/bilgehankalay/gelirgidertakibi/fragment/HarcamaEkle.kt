package com.bilgehankalay.gelirgidertakibi.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentHarcamaEkleBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HarcamaEkle : Fragment() {
    private lateinit var binding : FragmentHarcamaEkleBinding

    private lateinit var gelirGiderTakipDatabase: GelirGiderTakipDatabase

    var cal = Calendar.getInstance()

    var harcamaTipiAdListesi : ArrayList<String> = arrayListOf()
    var harcamaTipleri : ArrayList<HarcamaTipi> = arrayListOf()

    var seciliHarcamaTipi : HarcamaTipi? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(requireContext())!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHarcamaEkleBinding.inflate(inflater,container,false)
        return binding.root
    }
    private fun updateDateInView() {
        val myFormat = requireContext().getString(R.string.date_format)
        val sdf = SimpleDateFormat(myFormat)
        binding.editTextDateYinelemeBitisHarcama.setText(sdf.format(cal.time))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        temizle()
        harcamaTipleriGetir()
        loadSpinner()
        tekrarSpinnerYukle()

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        binding.also {
            it.buttonTemizleHarcama.setOnClickListener {
                temizle()
            }
            it.buttonEkleHarcama.setOnClickListener {
                gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderEkle(harcamaOlustur())
                Toast.makeText(requireContext(),"Harcama eklendi", Toast.LENGTH_LONG).show()
                temizle()
                findNavController().navigate(R.id.action_harcamaEkle_to_ozet)
            }
            it.switchDuzenliMiHarcama.setOnCheckedChangeListener{_, stateBool ->
                if (stateBool){
                    binding.spinnerYineleHarcama.visibility = View.VISIBLE
                    binding.textViewYineleHarcama.visibility = View.VISIBLE
                    binding.textViewYineleBitisHarcama.visibility = View.VISIBLE
                    binding.buttonTarihSecHarcama.visibility = View.VISIBLE
                    binding.editTextDateYinelemeBitisHarcama.visibility = View.VISIBLE
                }
                else{
                    binding.spinnerYineleHarcama.visibility = View.INVISIBLE
                    binding.textViewYineleHarcama.visibility = View.INVISIBLE
                    binding.textViewYineleBitisHarcama.visibility = View.INVISIBLE
                    binding.buttonTarihSecHarcama.visibility = View.INVISIBLE
                    binding.editTextDateYinelemeBitisHarcama.visibility = View.INVISIBLE
                }
            }
            it.spinnerHarcamaTipi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    seciliHarcamaTipi = harcamaTipleri[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    seciliHarcamaTipi = null
                }
            }

            it.buttonTarihSecHarcama.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View) {
                    DatePickerDialog(requireContext(),
                        dateSetListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)).show()
                }
            })

        }
    }

    private fun harcamaTipleriGetir(){
        gelirGiderTakipDatabase.harcamaTipiDAO().tumHarcamaTipi().forEach {
            if (it != null){
                harcamaTipleri.add(it)
                harcamaTipiAdListesi.add(it.ad)

            }
        }
    }

    private fun tekrarSpinnerYukle(){
        binding.spinnerYineleHarcama.visibility = View.INVISIBLE
        binding.textViewYineleHarcama.visibility = View.INVISIBLE
        binding.textViewYineleBitisHarcama.visibility = View.INVISIBLE
        binding.buttonTarihSecHarcama.visibility = View.INVISIBLE
        binding.editTextDateYinelemeBitisHarcama.visibility = View.INVISIBLE

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tekrar_secenekler,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerYineleHarcama.adapter = adapter
        }
    }

    private fun temizle(){
        harcamaTipleri.clear()
        harcamaTipiAdListesi.clear()
        binding.also {
            it.editTextAdHarcama.setText("")
            it.editTextAciklamaHarcama.setText("")
            it.editTextMiktarHarcama.setText("")
            it.switchDuzenliMiHarcama.isChecked = false


        }
    }

    private fun loadSpinner(){
        val adapter : ArrayAdapter<*>
        adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            harcamaTipiAdListesi
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHarcamaTipi.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun harcamaOlustur() : GelirGider {
        binding.also {
            var harcamaAdi = it.editTextAdHarcama.text.toString()
            if (it.editTextAdHarcama.text.isEmpty())
                harcamaAdi = requireContext().getString(R.string.isimsiz_harcama)

            val harcamaTipiId = seciliHarcamaTipi!!.id



            val miktar = it.editTextMiktarHarcama.text.toString().toDouble()
            val aciklama = it.editTextAciklamaHarcama.text.toString()
            val eklenme = System.currentTimeMillis();

            val duzenli_mi = it.switchDuzenliMiHarcama.isChecked
            if (duzenli_mi){

            }

            return GelirGider(
                tip = 1,
                ad = harcamaAdi,
                miktar = miktar,
                aciklama = aciklama,
                eklenme_zamani = eklenme,
                duzenli_mi = duzenli_mi,
                harcama_tipi_id = harcamaTipiId,
            )

        }
    }

}