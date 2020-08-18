package github.informramiz.commonuiviews.bottombar

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.*
import github.informramiz.commonuiviews.R
import github.informramiz.commonuiviews.databinding.ViewStackedBottomNavigationBinding


/**
 * Created by Ramiz Raja on 18/08/2020.
 */
class StackedBottomNavigation @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private val viewBinding =
        ViewStackedBottomNavigationBinding.inflate(LayoutInflater.from(context), this)

    init {
        setBackgroundResource(R.color.bottom_nav_background_color)
        addSampleOptions()
    }

    private fun addSampleOptions() {
        addNestedOptions(listOf("Secure Zone", "Employee Zone", "Guest Zone"))
    }

    private fun addNestedOptions(options: List<String>) {
        val viewIds = IntArray(options.size)
        options.forEachIndexed { index, option ->
            val viewId = addNestedOption(option)
            viewIds[index] = viewId
        }

        viewBinding.flowTopItems.isVisible = viewIds.isNotEmpty()
        viewBinding.flowTopItems.referencedIds = viewIds
        markFirstItemAsSelected()
    }

    private fun addNestedOption(optionText: String): Int {
        val textView = TextView(context).apply {
            layoutParams =
                LayoutParams(
                    context.resources.getDimensionPixelSize(R.dimen.bottom_nav_top_option_width),
                    LayoutParams.WRAP_CONTENT
                )
            text = optionText
            id = View.generateViewId()
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            setPadding(context.resources.getDimensionPixelSize(R.dimen.bottom_nav_top_option_padding))
            setBackgroundResource(R.drawable.bottom_nav_option_item_selector)
        }

        textView.setOnClickListener(this)
        addView(textView)
        return textView.id
    }

    private fun markFirstItemAsSelected() {
        if (viewBinding.flowTopItems.referencedIds.isEmpty()) return
        findViewById<View>(viewBinding.flowTopItems.referencedIds.first()).isSelected = true
    }

    override fun onClick(view: View) {
        clearSelection()
        view.isSelected = true
    }

    private fun clearSelection() {
        for (id in viewBinding.flowTopItems.referencedIds) {
            findViewById<TextView>(id).isSelected = false
        }
    }
}