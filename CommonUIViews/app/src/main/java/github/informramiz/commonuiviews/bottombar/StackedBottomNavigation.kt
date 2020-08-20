package github.informramiz.commonuiviews.bottombar

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
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
    private val menuInflater = MenuInflater(context)
    private val menu: Menu = PopupMenu(context, this).menu
    @MenuRes
    private var menuResId: Int = -1
    init {
        setBackgroundResource(R.color.bottom_nav_background_color)
        context.withStyledAttributes(attrs, R.styleable.StackedBottomNavigation) {
            menuResId = getResourceId(R.styleable.StackedBottomNavigation_menu, menuResId)
            if (menuResId == -1) {
                throw IllegalArgumentException("menu resource must be provided")
            }
        }
        menuInflater.inflate(menuResId, menu)
        addMainOptions()
        registerListeners()
        addSampleOptions()
    }

    private fun addMainOptions() {
        menu.children.forEach { addOptionToBottomNavigationView(it) }
    }

    private fun addOptionToBottomNavigationView(first: MenuItem) {
        viewBinding.bottomNavigationView.menu.add(
            first.groupId,
            first.itemId,
            first.order,
            first.title
        ).apply {
            icon = first.icon
        }
    }

    private fun addSampleOptions() {
        addNestedOptions(listOf("Secure Zone", "Employee Zone", "Guest Zone"))
    }

    private fun registerListeners() {
        viewBinding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            true
        }
    }

    private fun addNestedOptions(options: List<String>) {
        clearExistingNestedOptions()
        val viewIds = IntArray(options.size)
        options.forEachIndexed { index, option ->
            val viewId = addNestedOption(option)
            viewIds[index] = viewId
        }

        viewBinding.flowTopItems.isVisible = viewIds.isNotEmpty()
        viewBinding.flowTopItems.referencedIds = viewIds
        markFirstItemAsSelected()
    }

    private fun clearExistingNestedOptions() {
        viewBinding.flowTopItems.referencedIds.forEach { id ->
            removeView(findViewById(id))
        }
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