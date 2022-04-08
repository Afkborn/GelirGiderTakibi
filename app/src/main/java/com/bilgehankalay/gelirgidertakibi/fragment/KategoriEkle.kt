package com.bilgehankalay.gelirgidertakibi.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bilgehankalay.gelirgidertakibi.Database.GelirGiderTakipDatabase
import com.bilgehankalay.gelirgidertakibi.Model.HarcamaTipi
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentKategoriEkleBinding
import java.io.*
import java.util.*


class KategoriEkle : Fragment() {
    private lateinit var binding : FragmentKategoriEkleBinding

    private lateinit var secilenBitmap : Bitmap

    private lateinit var gelirGiderTakipDatabase: GelirGiderTakipDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gelirGiderTakipDatabase = GelirGiderTakipDatabase.getirGelirGiderTakipDatabase(requireContext())!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentKategoriEkleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.constraintLayoutIco.visibility = View.INVISIBLE
        binding.switchSimgeVar.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (p1){
                    binding.constraintLayoutIco.visibility = View.VISIBLE
                }
                else{
                    binding.constraintLayoutIco.visibility = View.INVISIBLE
                }
            }

        })
        binding.buttonSimgeSec.setOnClickListener {
            simgeSec()
        }
        binding.fabEkle.setOnClickListener {
            if(verikontrol()){
                val ad = binding.editTextKategoriAd.text.toString()
                val olusanHarcamaTipi : HarcamaTipi
                if (binding.switchSimgeVar.isChecked){
                    try {
                        val fileLoc = saveImageToExternalStorage(secilenBitmap)
                        olusanHarcamaTipi = HarcamaTipi(ad= ad, has_drawable = true, drawable_name =fileLoc.toString(), is_custom = true)
                        gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiEkle(olusanHarcamaTipi)
                        Toast.makeText(requireContext(),"BAŞARILI",Toast.LENGTH_LONG).show()

                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                else{
                    olusanHarcamaTipi = HarcamaTipi(ad= ad, has_drawable = false, is_custom = true)
                    gelirGiderTakipDatabase.harcamaTipiDAO().harcamaTipiEkle(olusanHarcamaTipi)
                }


            }
        }
    }

    private fun saveImageToExternalStorage(bitmap:Bitmap):Uri{
        val path = Environment.getDataDirectory()
        val file = File(path,"/data/com.bilgehankalay.gelirgidertakibi/drawable/${UUID.randomUUID()}.png")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image path to uri
        return Uri.parse(file.absolutePath)
    }


    private fun verikontrol() : Boolean{
        if (binding.editTextKategoriAd.text.isNullOrEmpty())
            return false
        return true
    }

    private fun simgeSec(){
        if(ContextCompat.checkSelfPermission(requireContext().applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==1){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==2 && resultCode == Activity.RESULT_OK && data !=null){
            val secilenGorsel = data.data
            try {
                if(secilenGorsel != null){
                    if (Build.VERSION.SDK_INT >= 28){
                        val source = ImageDecoder.createSource(requireContext().contentResolver,secilenGorsel)
                        secilenBitmap = ImageDecoder.decodeBitmap(source)
                        binding.imageView.setImageBitmap(secilenBitmap)
                    }
                    else{
                        secilenBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,secilenGorsel)
                        binding.imageView.setImageBitmap(secilenBitmap)
                    }
                }
            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }

    }



}