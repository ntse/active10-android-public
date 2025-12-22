package com.phe.betterhealth.components.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.common.setMarginTop
import com.phe.betterhealth.widgets.databinding.BhSettingsItemAboutBinding
import com.phe.betterhealth.widgets.databinding.BhSettingsItemCheckboxBinding
import com.phe.betterhealth.widgets.databinding.BhSettingsItemDescriptionBinding
import com.phe.betterhealth.widgets.databinding.BhSettingsItemHeaderBinding
import com.phe.betterhealth.widgets.databinding.BhSettingsItemItemBinding
import com.phe.betterhealth.widgets.databinding.BhSettingsItemResetBinding
import com.phe.betterhealth.widgets.databinding.BhSettingsItemSwitchBinding
import com.phe.betterhealth.widgets.databinding.BhSettingsItemVersionBinding
import com.phe.betterhealth.widgets.utils.textColor

class SettingsAdapter : ListAdapter<SettingsItem, RecyclerView.ViewHolder>(differ) {

    override fun getItemViewType(position: Int) = getItem(position).type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SettingsViewType.SECTION.ordinal -> SectionViewHolder(
                BhSettingsItemHeaderBinding.inflate(inflater, parent, false)
            )

            SettingsViewType.MENU_ITEM.ordinal -> ItemViewHolder(
                BhSettingsItemItemBinding.inflate(inflater, parent, false)
            )

            SettingsViewType.ABOUT.ordinal -> AboutViewHolder(
                BhSettingsItemAboutBinding.inflate(inflater, parent, false)
            )

            SettingsViewType.VERSION.ordinal -> VersionViewHolder(
                BhSettingsItemVersionBinding.inflate(inflater, parent, false)
            )

            SettingsViewType.DESCRIPTION.ordinal -> DescriptionViewHolder(
                BhSettingsItemDescriptionBinding.inflate(inflater, parent, false)
            )

            SettingsViewType.SWITCH.ordinal -> SettingsItemSwitchViewHolder(
                BhSettingsItemSwitchBinding.inflate(inflater, parent, false)
            )

            SettingsViewType.CHECKBOX.ordinal -> SettingsItemCheckboxViewHolder(
                BhSettingsItemCheckboxBinding.inflate(inflater, parent, false)
            )

            SettingsViewType.RESET_BUTTON.ordinal -> ResetButtonViewHolder(
                BhSettingsItemResetBinding.inflate(inflater, parent, false)
            )

            else -> throw IllegalStateException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SectionViewHolder -> bindSectionViewHolder(holder, item as SettingsSection)
            is ItemViewHolder -> bindItemViewHolder(holder, item as SettingsMenuItem)
            is AboutViewHolder -> bindAboutViewHolder(holder, item as SettingsAbout)
            is VersionViewHolder -> bindVersionViewHolder(holder, item as SettingsVersion)
            is ResetButtonViewHolder -> bindResetItemViewHolder(holder, item as SettingsResetButton)
            is DescriptionViewHolder -> bindDescriptionViewHolder(
                holder,
                item as SettingsMenuDescriptionItem
            )

            is SettingsItemSwitchViewHolder -> bindSettingsMenuSwitchViewHolder(
                holder,
                item as SettingsMenuSwitchItem
            )

            is SettingsItemCheckboxViewHolder -> bindSettingsMenuCheckboxViewHolder(
                holder,
                item as SettingsMenuCheckboxItem
            )
        }
    }

    private fun bindSectionViewHolder(holder: SectionViewHolder, item: SettingsSection) {
        with(holder.binding) {
            settingItem = item
            executePendingBindings()

            item.callback?.let { callback ->
                root.setOnClickListener {
                    callback.invoke()
                }
            }
        }
    }

    private fun bindItemViewHolder(holder: ItemViewHolder, item: SettingsMenuItem) {
        with(holder.binding) {
            settingItem = item
            executePendingBindings()

            if (item.enabled) {
                root.setOnClickListener { item.callback() }
            } else {
                valueView1.textColor = R.color.bhTextSecondary
                chevron.alpha = 0f
            }

            holder.binding.titleView.apply {
                typeface = if (item.isTitleBold) {
                    ResourcesCompat.getFont(context, R.font.roboto_bold)
                } else {
                    ResourcesCompat.getFont(context, R.font.roboto_regular)
                }
            }
        }
    }

    private fun bindAboutViewHolder(holder: AboutViewHolder, about: SettingsAbout) {
        with(holder.binding) {
            settingItem = about
            executePendingBindings()
            termsButton.setOnClickListener { about.termsCallback() }
            policyButton.setOnClickListener { about.policyCallback() }
            a11yButton.setOnClickListener { about.accessibilityCallback() }
        }
    }

    private fun bindVersionViewHolder(holder: VersionViewHolder, item: SettingsVersion) {
        with(holder.binding) {
            settingItem = item
            executePendingBindings()
            item.callback?.let { callback ->
                root.setOnClickListener {
                    callback.invoke()
                }
            }
        }
    }

    private fun bindDescriptionViewHolder(
        holder: DescriptionViewHolder,
        item: SettingsMenuDescriptionItem
    ) {
        with(holder.binding) {
            settingItem = item
            executePendingBindings()
            root.setOnClickListener { item.callback() }
            titleView.setMarginTop(if (item.showTopMargin) 18 else 0)
        }
    }

    private fun bindSettingsMenuSwitchViewHolder(
        holder: SettingsItemSwitchViewHolder,
        item: SettingsMenuSwitchItem
    ) {
        with(holder.binding) {
            settingItem = item

            holder.binding.switchItem.isEnabled = item.enabled

            holder.binding.switchItem.isChecked = item.value

            holder.binding.divider.isVisible = item.isDividerVisible

            val value = if (item.value) "enabled" else "disabled"
            root.contentDescription = "${item.name}, $value"

            root.setOnClickListener {
                if (item.enabled) {
                    holder.binding.switchItem.isChecked = !item.value
                }
                item.callback(!item.value)
            }

            holder.binding.titleView.apply {
                typeface = if (item.isTitleBold) {
                    ResourcesCompat.getFont(context, R.font.roboto_bold)
                } else {
                    ResourcesCompat.getFont(context, R.font.roboto_regular)
                }
            }

            holder.binding.switchState.apply {
                if (item.isTextSwitchStateVisible) {
                    text = if (item.value) "ON" else "OFF"
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }

            executePendingBindings()
        }
    }

    private fun bindSettingsMenuCheckboxViewHolder(
        holder: SettingsItemCheckboxViewHolder,
        item: SettingsMenuCheckboxItem
    ) {
        with(holder.binding) {
            settingItem = item
            executePendingBindings()

            checkboxItem.setOnCheckedChangeListener { _, checked ->
                item.callback(checked)
            }
        }
    }

    private fun bindResetItemViewHolder(holder: ResetButtonViewHolder, item: SettingsResetButton) {
        with(holder.binding) {
            settingItem = item
            executePendingBindings()
            resetButton.setOnClickListener { item.callback() }
        }
    }

    private class SectionViewHolder(val binding: BhSettingsItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ItemViewHolder(val binding: BhSettingsItemItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class AboutViewHolder(val binding: BhSettingsItemAboutBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class VersionViewHolder(val binding: BhSettingsItemVersionBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class DescriptionViewHolder(val binding: BhSettingsItemDescriptionBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class SettingsItemSwitchViewHolder(val binding: BhSettingsItemSwitchBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class SettingsItemCheckboxViewHolder(val binding: BhSettingsItemCheckboxBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ResetButtonViewHolder(val binding: BhSettingsItemResetBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private val differ = object : DiffUtil.ItemCallback<SettingsItem>() {
            override fun areItemsTheSame(oldItem: SettingsItem, newItem: SettingsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SettingsItem, newItem: SettingsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
