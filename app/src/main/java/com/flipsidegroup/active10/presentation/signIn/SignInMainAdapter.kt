package com.flipsidegroup.active10.presentation.signIn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.api.ScreenMedia
import com.flipsidegroup.active10.databinding.ItemSignInIconDescriptionBinding
import com.flipsidegroup.active10.utils.extractAnchorTag
import com.flipsidegroup.active10.utils.handleTextLink
import com.flipsidegroup.active10.utils.removeAnchorTag
import com.flipsidegroup.active10.utils.setTextHtml

data class IconWithDescription(
    val id: Int,
    val icon: ScreenMedia?,
    val description: String
)

class SignInMainAdapter()
    : ListAdapter<IconWithDescription, SignInMainAdapter.SignInMainVH>(DiffCallback()) {

    private var list: MutableList<IconWithDescription> = mutableListOf()
    var linkClickListener: () -> Unit = {}

    fun addData(content: ScreenContent) {
        for (num in 1..content.properties.size) {
            content.properties.firstOrNull { it.key == "reason_${num}_text" }?.let { description ->
                if (description.key == "reason_${num}_text") {
                    val icon = content.media.firstOrNull { icon -> icon.label == "reason_${num}_icon" }
                    list.add(IconWithDescription(num, icon, description.value))
                }
            }
        }
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignInMainVH {
        val inflater = LayoutInflater.from(parent.context)
        val item = ItemSignInIconDescriptionBinding.inflate(inflater, parent, false)
        return SignInMainVH(item)
    }

    override fun onBindViewHolder(holder: SignInMainVH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SignInMainVH(val binding: ItemSignInIconDescriptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal fun bind(item: IconWithDescription) {
            with(binding) {
                image.load(item.icon?.url)
                description.setTextHtml(item.description.removeAnchorTag())
                val linkContent = item.description.extractAnchorTag()
                link.isVisible = linkContent.isNotBlank()
                link.handleTextLink(linkContent) {
                    when (it) {
                        "new_features" -> {
                            linkClickListener()
                        }
                    }
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<IconWithDescription>() {

        override fun areContentsTheSame(oldItem: IconWithDescription, newItem: IconWithDescription) =
            oldItem.id == newItem.id

        override fun areItemsTheSame(oldItem: IconWithDescription, newItem: IconWithDescription) =
            oldItem == newItem

    }
}