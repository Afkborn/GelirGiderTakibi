package com.yasaryasar.gelirgidertakibi.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.yasaryasar.gelirgidertakibi.R

class SearchDialogFragment(itemsArrayList : ArrayList<String>, private val seciliItemListener : SeciliItemListener) : DialogFragment() {
    var items = arrayOf<CharSequence>()
    var charSequences: ArrayList<CharSequence> = ArrayList()
    val itemsArrayList = itemsArrayList

    interface SeciliItemListener{
        fun seciliFiltre(seciliItem : ArrayList<String>)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        itemsLoad()

        return activity?.let { it ->
            val selectedItems = ArrayList<Int>()
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.aciklama)
                .setMultiChoiceItems(items, null,
                    DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                        if (isChecked) {
                            selectedItems.add(which)
                        } else if (selectedItems.contains(which)) {
                            selectedItems.remove(Integer.valueOf(which))
                        }
                    })

                .setPositiveButton(R.string.filtrele,
                    DialogInterface.OnClickListener { dialog, id ->
                        val selectedItemStr : ArrayList<String> = arrayListOf()
                        selectedItems.forEach { item ->
                            selectedItemStr.add(charSequences[item].toString())
                        }
                        seciliItemListener.seciliFiltre(selectedItemStr)
                    }
                )
                .setNegativeButton(R.string.iptal,
                    DialogInterface.OnClickListener { dialog, id ->
                    }
                )
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    fun itemsLoad(){
        charSequences.clear()
        charSequences.add(requireContext().getString(R.string.duzenli_islem))
        charSequences.add(requireContext().getString(R.string.gelirlerim))
        charSequences.add(requireContext().getString(R.string.giderlerim))
        itemsArrayList.forEach {
            charSequences.add(it)
        }
        items = charSequences.toTypedArray()

    }

}

