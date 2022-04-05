package com.bilgehankalay.gelirgidertakibi.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.GelirGider
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentGelirEkleBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GelirEkle : Fragment() {
    private lateinit var binding : FragmentGelirEkleBinding

    private lateinit var gelirGiderTakipDatabase: GelirGiderTakipDatabase

    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(requireContext())!!

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGelirEkleBinding.inflate(inflater,container,false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            it.buttonTemizleGelir.setOnClickListener {
                temizle()
            }
            it.switchDuzenliMiGelir.setOnCheckedChangeListener{_, stateBool ->
                if (stateBool){
                    binding.spinnerYinele.visibility = View.VISIBLE
                    binding.textViewYinele.visibility = View.VISIBLE
                    binding.textViewYineleBitis.visibility = View.VISIBLE
                    binding.editTextDateYinelemeBitis.visibility = View.VISIBLE
                    binding.buttonTarihSec.visibility = View.VISIBLE
                }
                else{
                    binding.spinnerYinele.visibility = View.INVISIBLE
                    binding.textViewYinele.visibility = View.INVISIBLE
                    binding.textViewYineleBitis.visibility = View.INVISIBLE
                    binding.buttonTarihSec.visibility = View.INVISIBLE
                    binding.editTextDateYinelemeBitis.visibility = View.INVISIBLE
                }
            }
            it.buttonEkleGelir.setOnClickListener {
                val gelenGelir = gelirOlustur()

                gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderEkle(gelenGelir)

                //GET LAST INSERT ROW ID
                val sonEklenenGelir = gelirGiderTakipDatabase.gelirGiderDAO().eklenmeZamaniGelirGider(gelenGelir.eklenme_zamani)
                if (sonEklenenGelir != null && gelenGelir.duzenli_mi == true){
                    val eklenecekGelirler : ArrayList<GelirGider> = arrayListOf()
                    // gelir eklenmiş ID bilgisini almışız, düzenli bilgisi var
                    when (gelenGelir.tekrar_tipi){
                        //0 => Hergün, 1 => Hafta içi, 2 => Hafta sonu, 3 => Her Hafta, 4 => Her 2 haftada bir, 5 => Her ay
                        0 -> {
                            val oneDay = 86400000
                            var kacGunEklenecek = 0
                            var gelirEklenmeTarihi = gelenGelir.eklenme_zamani
                            while (gelirEklenmeTarihi <= gelenGelir.bitis_tarihi!!){
                                if (gelirEklenmeTarihi + oneDay <= gelenGelir.bitis_tarihi!!){
                                    kacGunEklenecek += 1
                                    val eklenecekGelir = GelirGider(
                                        tip = 3,
                                        ad = gelenGelir.ad,
                                        miktar = gelenGelir.miktar,
                                        aciklama = gelenGelir.aciklama,
                                        eklenme_zamani = gelirEklenmeTarihi + oneDay,
                                        aktif_pasif = gelenGelir.aktif_pasif,
                                        bitis_tarihi = gelenGelir.bitis_tarihi,
                                        ana_harcama = sonEklenenGelir.id,
                                        duzenli_mi = gelenGelir.duzenli_mi,
                                        tekrar_tipi = gelenGelir.tekrar_tipi,
                                        eklenmis_mi = false
                                    )
                                    eklenecekGelirler.add(eklenecekGelir)
                                }
                                gelirEklenmeTarihi += oneDay
                            }
                            Log.e("LOG","Toplam eklenecek gün sayısı $kacGunEklenecek")
                            eklenecekGelirler.forEach {
                                gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderEkle(it)
                            }
                        }
                        1 -> {

                        }
                        2 -> {}
                        3 -> {}
                        4 -> {}
                        5 -> {}
                    }
                }

                Toast.makeText(requireContext(),"Gelir eklendi",Toast.LENGTH_LONG).show()
                temizle()
                findNavController().navigate(R.id.action_gelirEkle_to_ozet)
            }
            it.buttonTarihSec.setOnClickListener(object : View.OnClickListener {
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

    private fun temizle(){
        binding.also {
            it.editTextAciklamaGelir.setText("")
            it.editTextAdGelir.setText("")
            it.editTextMiktarGelir.setText("")
            it.radioGroup.clearCheck()
            it.switchDuzenliMiGelir.isChecked = false
        }
    }

    private fun tekrarSpinnerYukle(){
        binding.spinnerYinele.visibility = View.INVISIBLE
        binding.textViewYinele.visibility = View.INVISIBLE
        binding.textViewYineleBitis.visibility = View.INVISIBLE
        binding.editTextDateYinelemeBitis.visibility = View.INVISIBLE
        binding.buttonTarihSec.visibility = View.INVISIBLE
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tekrar_secenekler,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerYinele.adapter = adapter
        }
    }
    private fun updateDateInView() {
        val myFormat = requireContext().getString(R.string.date_format)
        val sdf = SimpleDateFormat(myFormat)
        binding.editTextDateYinelemeBitis.setText(sdf.format(cal.time))
    }


    private fun gelirOlustur() : GelirGider{
        binding.also {
            var harcamaAdi = it.editTextAdGelir.text.toString()
            if (it.editTextAdGelir.text.isEmpty())
                harcamaAdi = requireContext().getString(R.string.isimsiz_gelir)

            var aktif_pasif = false
            if (it.radioButtonAktif.id == it.radioGroup.checkedRadioButtonId){
                aktif_pasif = true
            }

            val miktar = it.editTextMiktarGelir.text.toString().toDouble()
            val aciklama = it.editTextAciklamaGelir.text.toString()
            val eklenme = System.currentTimeMillis()

            val duzenli_mi = it.switchDuzenliMiGelir.isChecked
            var tekrar_tipi: Int? = null
            var bitis_tarihi : Long? = null
            if (duzenli_mi){
                //0 => Hergün, 1 => Hafta içi, 2 => Hafta sonu, 3 => Her Hafta, 4 => Her 2 haftada bir, 5 => Her ay
                tekrar_tipi = binding.spinnerYinele.selectedItemPosition
                bitis_tarihi = cal.timeInMillis + 100000
            }

            return GelirGider(
                tip = 0,
                ad = harcamaAdi,
                miktar = miktar,
                aciklama = aciklama,
                aktif_pasif = aktif_pasif,
                eklenme_zamani = eklenme,
                duzenli_mi = duzenli_mi,
                tekrar_tipi = tekrar_tipi,
                bitis_tarihi = bitis_tarihi
            )

        }
    }

}