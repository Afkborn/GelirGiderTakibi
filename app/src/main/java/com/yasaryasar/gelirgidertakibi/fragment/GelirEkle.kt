package com.yasaryasar.gelirgidertakibi.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yasaryasar.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.yasaryasar.gelirgidertakibi.Model.GelirGider
import com.yasaryasar.gelirgidertakibi.R
import com.yasaryasar.gelirgidertakibi.databinding.FragmentGelirEkleBinding
import java.text.SimpleDateFormat
import java.util.*


class GelirEkle : Fragment() {
    private lateinit var binding : FragmentGelirEkleBinding
    private lateinit var gelirGiderTakipDatabase: GelirGiderTakipDatabase
    val oneDay = 86400000
    val oneWeek = oneDay * 7
    val oneMounth = oneWeek * 4
    val oneYear = oneMounth * 12

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
                if (girdiKontrol()){
                    val gelenGelir = gelirOlustur()
                    gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderEkle(gelenGelir)

                    val sonEklenenGelir = gelirGiderTakipDatabase.gelirGiderDAO().eklenmeZamaniGelirGider(gelenGelir.eklenme_zamani)
                    if (sonEklenenGelir != null && gelenGelir.duzenli_mi == true){
                        //YİNELENEN GELİR
                        val eklenecekGelirler : ArrayList<GelirGider> = arrayListOf()
                        var kacGunEklenecek = 0
                        when (gelenGelir.tekrar_tipi){
                            //0 => Hergün, 1 => Hafta içi, 2 => Hafta sonu, 3 => Her Hafta, 4 => Her 2 haftada bir, 5 => Her ay, 6 => Her yıl, 7 => ÖZEL ???
                            0 -> {
                                var gelirEklenmeTarihi = gelenGelir.eklenme_zamani
                                while (gelirEklenmeTarihi <= gelenGelir.bitis_tarihi!!){
                                    if (gelirEklenmeTarihi + oneDay <= gelenGelir.bitis_tarihi!!){
                                        kacGunEklenecek += 1
                                        val eklenecekGelir = yinelenenGelirOlustur(gelenGelir,gelirEklenmeTarihi,sonEklenenGelir)
                                        eklenecekGelirler.add(eklenecekGelir)
                                    }
                                    gelirEklenmeTarihi += oneDay
                                }
                            }
                            1 -> {
                                var harcamaEklenmeTarihi = gelenGelir.eklenme_zamani
                                while (harcamaEklenmeTarihi <= gelenGelir.bitis_tarihi!!){
                                    if (harcamaEklenmeTarihi + oneDay <= gelenGelir.bitis_tarihi!!){
                                        val simpleDate = SimpleDateFormat("u")
                                        val myDate = Date(harcamaEklenmeTarihi+oneDay)
                                        if (simpleDate.format(myDate).toInt() <= 5){
                                            kacGunEklenecek += 1
                                            val eklenecekGelir = yinelenenGelirOlustur(gelenGelir,harcamaEklenmeTarihi,sonEklenenGelir)
                                            eklenecekGelirler.add(eklenecekGelir)
                                        }
                                    }
                                    harcamaEklenmeTarihi += oneDay
                                }
                            }
                            2 -> {
                                var harcamaEklenmeTarihi = gelenGelir.eklenme_zamani
                                while (harcamaEklenmeTarihi <= gelenGelir.bitis_tarihi!!){
                                    if (harcamaEklenmeTarihi + oneDay <= gelenGelir.bitis_tarihi!!){
                                        val simpleDate = SimpleDateFormat("u")
                                        val myDate = Date(harcamaEklenmeTarihi+oneDay)
                                        if (simpleDate.format(myDate).toInt() > 5){
                                            kacGunEklenecek += 1
                                            val eklenecekGelir = yinelenenGelirOlustur(gelenGelir,harcamaEklenmeTarihi,sonEklenenGelir)
                                            eklenecekGelirler.add(eklenecekGelir)
                                        }
                                    }
                                    harcamaEklenmeTarihi += oneDay
                                }
                            }
                            3 -> {
                                var gelirEklenmeTarihi = gelenGelir.eklenme_zamani
                                while (gelirEklenmeTarihi <= gelenGelir.bitis_tarihi!!){
                                    if (gelirEklenmeTarihi + oneWeek <= gelenGelir.bitis_tarihi!!){
                                        kacGunEklenecek += 1
                                        val eklenecekGelir = yinelenenGelirOlustur(gelenGelir,gelirEklenmeTarihi,sonEklenenGelir)
                                        eklenecekGelirler.add(eklenecekGelir)
                                    }
                                    gelirEklenmeTarihi += oneWeek
                                }
                            }
                            4 -> {
                                var gelirEklenmeTarihi = gelenGelir.eklenme_zamani
                                while (gelirEklenmeTarihi <= gelenGelir.bitis_tarihi!!){
                                    if (gelirEklenmeTarihi + (oneWeek * 2) <= gelenGelir.bitis_tarihi!!){
                                        kacGunEklenecek += 1
                                        val eklenecekGelir = yinelenenGelirOlustur(gelenGelir,gelirEklenmeTarihi,sonEklenenGelir)
                                        eklenecekGelirler.add(eklenecekGelir)
                                    }
                                    gelirEklenmeTarihi += oneWeek * 2
                                }
                            }
                            5 -> {
                                var gelirEklenmeTarihi = gelenGelir.eklenme_zamani
                                while (gelirEklenmeTarihi <= gelenGelir.bitis_tarihi!!){
                                    if (gelirEklenmeTarihi + oneMounth  <= gelenGelir.bitis_tarihi!!){
                                        kacGunEklenecek += 1
                                        val eklenecekGelir = yinelenenGelirOlustur(gelenGelir,gelirEklenmeTarihi,sonEklenenGelir)
                                        eklenecekGelirler.add(eklenecekGelir)
                                    }
                                    gelirEklenmeTarihi += oneMounth
                                }
                            }
                            6 -> {
                                var gelirEklenmeTarihi = gelenGelir.eklenme_zamani
                                while (gelirEklenmeTarihi <= gelenGelir.bitis_tarihi!!){
                                    if (gelirEklenmeTarihi + oneYear  <= gelenGelir.bitis_tarihi!!){
                                        kacGunEklenecek += 1
                                        val eklenecekGelir = yinelenenGelirOlustur(gelenGelir,gelirEklenmeTarihi,sonEklenenGelir)
                                        eklenecekGelirler.add(eklenecekGelir)
                                    }
                                    gelirEklenmeTarihi += oneYear
                                }
                            }
                        }
                        Log.e("LOG","Toplam eklenecek gün sayısı $kacGunEklenecek")
                        eklenecekGelirler.forEach {
                            gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderEkle(it)
                        }
                    }

                    Toast.makeText(requireContext(),"Gelir eklendi",Toast.LENGTH_LONG).show()
                    temizle()
                    ozetEkranaGec()
                }

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
    private fun girdiKontrol() : Boolean {
        if (binding.editTextMiktarGelir.text.isNullOrEmpty()){
            Toast.makeText(requireContext(),requireContext().getString(R.string.miktar_bos_olamaz),Toast.LENGTH_LONG).show()
            return false
        }
        if (binding.editTextAdGelir.text.isNullOrEmpty()){
            Toast.makeText(requireContext(),requireContext().getString(R.string.ad_bos_uyari),Toast.LENGTH_LONG).show()
        }

        return true

    }



    private fun ozetEkranaGec(){
        findNavController().navigate(R.id.action_gelirEkle_to_ozet)
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

    private fun yinelenenGelirOlustur(gelenGelir : GelirGider, gelirEklenmeTarihi : Long, sonEklenenGelir : GelirGider) : GelirGider {
        return GelirGider(
            tip = 3,
            ad = gelenGelir.ad,
            miktar = gelenGelir.miktar,
            aciklama = gelenGelir.aciklama,
            eklenme_zamani = gelirEklenmeTarihi + oneDay,
            bitis_tarihi = gelenGelir.bitis_tarihi,
            ana_harcama = sonEklenenGelir.id,
            duzenli_mi = gelenGelir.duzenli_mi,
            tekrar_tipi = gelenGelir.tekrar_tipi,
            eklenmis_mi = false,
            aktif_pasif = gelenGelir.aktif_pasif,
            yinelenen_mi = true
        )
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