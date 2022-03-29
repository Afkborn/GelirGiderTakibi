package com.bilgehankalay.gelirgidertakibi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bilgehankalay.gelirgidertakibi.R
import com.bilgehankalay.gelirgidertakibi.databinding.FragmentGelirEkleBinding


class GelirEkle : Fragment() {
    private lateinit var binding : FragmentGelirEkleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGelirEkleBinding.inflate(inflater,container,false)
        return binding.root

    }

}