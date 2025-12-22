package com.flipsidegroup.active10.presentation.todaywalk.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ItemTrophyBinding
import kotlin.math.max


class TrophyAdapter(private var setTargets: Int?, private var doneTargets: Int) :
    RecyclerView.Adapter<TrophyAdapter.TrophyVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrophyVH = TrophyVH(
        ItemTrophyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = setTargets?.let { max(it, doneTargets) } ?: doneTargets

    override fun onBindViewHolder(holder: TrophyVH, position: Int) {
        holder.bind()
    }

    fun createNewTrophy() {
        val newTarget = setTargets?.inc()
        setTargets = newTarget?.let { max(it, doneTargets + 1) } ?: doneTargets + 1
        notifyDataSetChanged()
    }

    fun updateNewTargets(targets: Int?) {
        this.setTargets = targets?.let { max(it, doneTargets) } ?: doneTargets
        notifyDataSetChanged()
    }

    fun updateDoneTarget(doneTargets: Int) {
        this.doneTargets = doneTargets
        notifyDataSetChanged()
    }

    inner class TrophyVH(
        private val binding: ItemTrophyBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val drawableIdRes = if (doneTargets > bindingAdapterPosition) {
                (R.drawable.ic_trophy_on)
            } else {
                R.drawable.ic_trophy_off
            }
            binding.trophyIV.setBackgroundResource(drawableIdRes)
        }
    }
}
