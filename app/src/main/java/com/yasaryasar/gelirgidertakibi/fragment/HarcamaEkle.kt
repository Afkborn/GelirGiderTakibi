package com.yasaryasar.gelirgidertakibi.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yasaryasar.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.yasaryasar.gelirgidertakibi.Model.GelirGider
import com.yasaryasar.gelirgidertakibi.Model.HarcamaTipi
import com.yasaryasar.gelirgidertakibi.R
import com.yasaryasar.gelirgidertakibi.databinding.FragmentHarcamaEkleBinding
import java.text.SimpleDateFormat
import java.util.*


class HarcamaEkle : Fragment() {
    private lateinit var binding : FragmentHarcamaEkleBinding

    private lateinit var gelirGiderTakipDatabase: GelirGiderTakipDatabase

    var cal = Calendar.getInstance()
    val oneDay = 86400000
    val oneWeek = oneDay * 7
    val oneMounth = oneWeek * 4
    val oneYear = oneMounth * 12
    var harcamaTipiAdListesi : ArrayList<String> = arrayListOf()
    var harcamaTipleri : ArrayList<HarcamaTipi> = arrayListOf()

    var seciliHarcamaTipi : HarcamaTipi? = null

    var eklenenKategoriId : Int = 0
    private val args : HarcamaEkleArgs by navArgs()

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
        kategoriSpinnerYukle()
        tekrarSpinnerYukle()

        eklenenKategoriId = args.kategoriId
        if (eklenenKategoriId != 0){
            val gelenHarcamaTipi = gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiGetirId(eklenenKategoriId)
            if (gelenHarcamaTipi != null){
                var counter = 0
                harcamaTipiAdListesi.forEach {
                    if (it == gelenHarcamaTipi.ad){
                        binding.spinnerHarcamaTipi.setSelection(counter)
                    }
                    counter++
                }
            }
        }

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
                harcamaTipleriGetir()
                kategoriSpinnerYukle()
                tekrarSpinnerYukle()
            }
            it.buttonEkleHarcama.setOnClickListener {
                if (girdiKontrol()){
                    val gelenHarcama = harcamaOlustur()
                    gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderEkle(gelenHarcama)
                    val sonEklenenHarcama = gelirGiderTakipDatabase.gelirGiderDAO().eklenmeZamaniGelirGider(gelenHarcama.eklenme_zamani)
                    if (sonEklenenHarcama != null && gelenHarcama.duzenli_mi == true){
                        val eklenecekHarcamalar : ArrayList<GelirGider> = arrayListOf()
                        var kacGunEklenecek = 0
                        when (gelenHarcama.tekrar_tipi){
                            //0 => Hergün, 1 => Hafta içi, 2 => Hafta sonu, 3 => Her Hafta, 4 => Her 2 haftada bir, 5 => Her ay, 6 => Her yıl, 7 => ÖZEL ???
                            0 -> {
                                var harcamaEklenmeTarihi = gelenHarcama.eklenme_zamani
                                while (harcamaEklenmeTarihi <= gelenHarcama.bitis_tarihi!!){
                                    if (harcamaEklenmeTarihi + oneDay <= gelenHarcama.bitis_tarihi!!){
                                        kacGunEklenecek += 1
                                        val eklenecekGelir = yinelenenHarcamaOlustur(gelenHarcama,harcamaEklenmeTarihi,sonEklenenHarcama)
                                        eklenecekHarcamalar.add(eklenecekGelir)
                                    }
                                    harcamaEklenmeTarihi += oneDay
                                }
                            }
                            1 -> {
                                var harcamaEklenmeTarihi = gelenHarcama.eklenme_zamani
                                while (harcamaEklenmeTarihi <= gelenHarcama.bitis_tarihi!!){
                                    if (harcamaEklenmeTarihi + oneDay <= gelenHarcama.bitis_tarihi!!){
                                        val simpleDate = SimpleDateFormat("u")
                                        val myDate = Date(harcamaEklenmeTarihi+oneDay)
                                        if (simpleDate.format(myDate).toInt() <= 5){
                                            kacGunEklenecek += 1
                                            val eklenecekGelir = yinelenenHarcamaOlustur(gelenHarcama,harcamaEklenmeTarihi,sonEklenenHarcama)
                                            eklenecekHarcamalar.add(eklenecekGelir)
                                        }
                                    }
                                    harcamaEklenmeTarihi += oneDay
                                }
                            }
                            2 -> {
                                var harcamaEklenmeTarihi = gelenHarcama.eklenme_zamani
                                while (harcamaEklenmeTarihi <= gelenHarcama.bitis_tarihi!!){
                                    if (harcamaEklenmeTarihi + oneDay <= gelenHarcama.bitis_tarihi!!){
                                        val simpleDate = SimpleDateFormat("u")
                                        val myDate = Date(harcamaEklenmeTarihi+oneDay)
                                        if (simpleDate.format(myDate).toInt() > 5){
                                            kacGunEklenecek += 1
                                            val eklenecekGelir = yinelenenHarcamaOlustur(gelenHarcama,harcamaEklenmeTarihi,sonEklenenHarcama)
                                            eklenecekHarcamalar.add(eklenecekGelir)
                                        }
                                    }
                                    harcamaEklenmeTarihi += oneDay
                                }
                            }
                            3 -> {
                                var harcamaEklenmeTarihi = gelenHarcama.eklenme_zamani
                                while (harcamaEklenmeTarihi <= gelenHarcama.bitis_tarihi!!){
                                    if (harcamaEklenmeTarihi + oneWeek <= gelenHarcama.bitis_tarihi!!){
                                        kacGunEklenecek += 1
                                        val eklenecekGelir = yinelenenHarcamaOlustur(gelenHarcama,harcamaEklenmeTarihi,sonEklenenHarcama)
                                        eklenecekHarcamalar.add(eklenecekGelir)
                                    }
                                    harcamaEklenmeTarihi += oneWeek
                                }
                            }
                            4 -> {
                                var harcamaEklenmeTarihi = gelenHarcama.eklenme_zamani
                                while (harcamaEklenmeTarihi <= gelenHarcama.bitis_tarihi!!){
                                    if (harcamaEklenmeTarihi + (oneWeek *2 ) <= gelenHarcama.bitis_tarihi!!){
                                        kacGunEklenecek += 1
                                        val eklenecekGelir = yinelenenHarcamaOlustur(gelenHarcama,harcamaEklenmeTarihi,sonEklenenHarcama)
                                        eklenecekHarcamalar.add(eklenecekGelir)
                                    }
                                    harcamaEklenmeTarihi += (oneWeek *2 )
                                }
                            }
                            5 -> {
                                var harcamaEklenmeTarihi = gelenHarcama.eklenme_zamani
                                while (harcamaEklenmeTarihi <= gelenHarcama.bitis_tarihi!!){
                                    if (harcamaEklenmeTarihi + oneMounth <= gelenHarcama.bitis_tarihi!!){
                                        kacGunEklenecek += 1
                                        val eklenecekGelir = yinelenenHarcamaOlustur(gelenHarcama,harcamaEklenmeTarihi,sonEklenenHarcama)
                                        eklenecekHarcamalar.add(eklenecekGelir)
                                    }
                                    harcamaEklenmeTarihi += oneMounth
                                }
                            }
                            6 -> {
                                var harcamaEklenmeTarihi = gelenHarcama.eklenme_zamani
                                while (harcamaEklenmeTarihi <= gelenHarcama.bitis_tarihi!!){
                                    if (harcamaEklenmeTarihi + oneYear <= gelenHarcama.bitis_tarihi!!){
                                        kacGunEklenecek += 1
                                        val eklenecekGelir = yinelenenHarcamaOlustur(gelenHarcama,harcamaEklenmeTarihi,sonEklenenHarcama)
                                        eklenecekHarcamalar.add(eklenecekGelir)
                                    }
                                    harcamaEklenmeTarihi += oneYear
                                }
                            }
                        }
                        Log.e("LOG","Toplam eklenecek gün sayısı $kacGunEklenecek")
                        eklenecekHarcamalar.forEach {
                            gelirGiderTakipDatabase.gelirGiderDAO().gelirGiderEkle(it)
                        }
                    }
                    Toast.makeText(requireContext(),"Harcama eklendi", Toast.LENGTH_LONG).show()
                    temizle()
                    ozetEkranaGec()
                }
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
                    if (harcamaTipiAdListesi[p2] == requireContext().getString(R.string.yeni_kategori_ekle)) {
                        seciliHarcamaTipi = null
                        kategoriEkleEkranaGec()
                        temizle()
                    }
                    else{
                        seciliHarcamaTipi = harcamaTipleri[p2]
                    }


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

    private fun kategoriEkleEkranaGec(){
        val gecisAction = HarcamaEkleDirections.harcamaEkleToKategoriEkle(gelinenEkran = 0)
        findNavController().navigate(gecisAction)
    }

    private fun girdiKontrol() : Boolean {
        //HATA
        if (binding.editTextMiktarHarcama.text.isNullOrEmpty()){
            Toast.makeText(requireContext(),requireContext().getString(R.string.miktar_bos_olamaz),Toast.LENGTH_LONG).show()
            return false
        }
        if (seciliHarcamaTipi == null){
            Toast.makeText(requireContext(),requireContext().getString(R.string.kategori_sec),Toast.LENGTH_LONG).show()
            return false
        }

        //UYARI
        if (binding.editTextAdHarcama.text.isNullOrEmpty()){
            Toast.makeText(requireContext(),requireContext().getString(R.string.ad_bos_uyari),Toast.LENGTH_LONG).show()
        }
        return true

    }

    private fun ozetEkranaGec(){
        findNavController().navigate(R.id.action_harcamaEkle_to_ozet)
    }

    private fun yinelenenHarcamaOlustur(gelenHarcama : GelirGider, gelirEklenmeTarihi : Long, sonEklenenHarcama : GelirGider) : GelirGider {
         return GelirGider(
             tip = 3,
             ad = gelenHarcama.ad,
             miktar = gelenHarcama.miktar,
             aciklama = gelenHarcama.aciklama,
             eklenme_zamani = gelirEklenmeTarihi + oneDay,
             bitis_tarihi = gelenHarcama.bitis_tarihi,
             ana_harcama = sonEklenenHarcama.id,
             duzenli_mi = gelenHarcama.duzenli_mi,
             tekrar_tipi = gelenHarcama.tekrar_tipi,
             eklenmis_mi = false,
             harcama_tipi_id = gelenHarcama.harcama_tipi_id,
             yinelenen_mi = true
        )
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

    private fun kategoriSpinnerYukle(){
        harcamaTipiAdListesi.add(requireContext().getString(R.string.yeni_kategori_ekle))
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
            var tekrar_tipi: Int? = null
            var bitis_tarihi : Long? = null
            if (duzenli_mi){
                //0 => Hergün, 1 => Hafta içi, 2 => Hafta sonu, 3 => Her Hafta, 4 => Her 2 haftada bir, 5 => Her ay
                tekrar_tipi = binding.spinnerYineleHarcama.selectedItemPosition
                bitis_tarihi = cal.timeInMillis + 100000
            }
            return GelirGider(
                tip = 1,
                ad = harcamaAdi,
                miktar = miktar,
                aciklama = aciklama,
                eklenme_zamani = eklenme,
                duzenli_mi = duzenli_mi,
                harcama_tipi_id = harcamaTipiId,
                tekrar_tipi = tekrar_tipi,
                bitis_tarihi = bitis_tarihi
            )

        }
    }

}