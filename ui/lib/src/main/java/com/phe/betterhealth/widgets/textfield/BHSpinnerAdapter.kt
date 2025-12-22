package com.phe.betterhealth.widgets.textfield

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import com.phe.betterhealth.widgets.R

class BHSpinnerAdapter(
    context: Context,
    resource: Int = R.layout.support_simple_spinner_dropdown_item,
    objects: List<String>
) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun getFilter() = object : Filter() {
        override fun performFiltering(constraint: CharSequence?) = null
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
    }
}
