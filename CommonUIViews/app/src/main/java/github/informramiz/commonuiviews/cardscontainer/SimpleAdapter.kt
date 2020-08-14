package github.informramiz.circleimageview

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter


/**
 * Created by Ramiz Raja on 12/08/2020.
 */
private val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}
class SimpleAdapter : ListAdapter<String, SimpleViewHolder>(DIFF_ITEM_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        return SimpleViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }
}