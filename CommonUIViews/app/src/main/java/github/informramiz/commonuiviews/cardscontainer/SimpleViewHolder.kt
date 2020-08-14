package github.informramiz.circleimageview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import github.informramiz.commonuiviews.databinding.ListItemBinding


/**
 * Created by Ramiz Raja on 12/08/2020.
 */
class SimpleViewHolder(private val viewBinding: ListItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    fun bind(text: String) {
        viewBinding.textView.text = text
    }

    companion object {
        fun create(parent: ViewGroup): SimpleViewHolder {
            return SimpleViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}