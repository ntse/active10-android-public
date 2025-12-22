package com.flipsidegroup.active10.presentation.licenses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.data.AcknowledgementLicense
import com.flipsidegroup.active10.databinding.ItemLicenseViewHolderBinding

class LicensesAdapter: ListAdapter<AcknowledgementLicense, LicensesAdapter.ViewHolder>(differ) {

    var onItemClickListener: (AcknowledgementLicense) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemLicenseViewHolderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.dependencyTitle.text = getItem(position).name ?: getItem(position).groupWithArtifact
        holder.binding.dependencySubTitle.text = if (getItem(position).name != null) getItem(position).groupWithArtifact else ""
        holder.binding.dependencySubTitle.isVisible = getItem(position).name != null
        holder.binding.dependencyLicense.text = getItem(position).license

        holder.binding.root.setOnClickListener {
            onItemClickListener(getItem(position))
        }
    }

    class ViewHolder(val binding: ItemLicenseViewHolderBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val differ = object : DiffUtil.ItemCallback<AcknowledgementLicense>() {
            override fun areItemsTheSame(
                oldItem: AcknowledgementLicense, newItem: AcknowledgementLicense
            ) = oldItem.groupWithArtifact == newItem.groupWithArtifact

            override fun areContentsTheSame(
                oldItem: AcknowledgementLicense, newItem: AcknowledgementLicense
            ) = newItem == oldItem
        }
    }

}