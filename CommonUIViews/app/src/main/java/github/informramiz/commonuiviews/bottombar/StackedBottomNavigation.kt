package github.informramiz.commonuiviews.bottombar

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.NavigationUI
import github.informramiz.commonuiviews.R
import github.informramiz.commonuiviews.databinding.ViewStackedBottomNavigationBinding
import java.lang.ref.WeakReference


/**
 * Created by Ramiz Raja on 18/08/2020.
 */
class StackedBottomNavigation @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {
    var onItemClickListener: ((itemId: MenuItem) -> Unit)? = null
    val menuItems: List<MenuItem>
        get() {
            return menu.children.flatMap {
                if (it.hasSubMenu()) {
                    sequenceOf(it) + it.subMenu.children
                } else {
                    emptySequence()
                }
            }.toList()
        }

    private val viewBinding =
        ViewStackedBottomNavigationBinding.inflate(LayoutInflater.from(context), this)
    private val menuInflater = MenuInflater(context)
    private val menu: Menu = PopupMenu(context, this).menu
    @MenuRes
    private var menuResId: Int = -1

    init {
        extractAttributes(context, attrs)
        loadMenuOptions()
        registerListeners()
    }

    private fun extractAttributes(context: Context, attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.StackedBottomNavigation) {
            menuResId = getResourceId(R.styleable.StackedBottomNavigation_menu, menuResId)
            if (menuResId == -1) {
                throw IllegalArgumentException("menu resource must be provided")
            }
        }
    }

    private fun loadMenuOptions() {
        menuInflater.inflate(menuResId, menu)
        addMainOptions()
        val first = menu.children.first()
        addNestedOptionsForMenu(first)
    }

    private fun registerListeners() {
        viewBinding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            val completeMenuItem = menu.findItem(menuItem.itemId)
            addNestedOptionsForMenu(completeMenuItem)
            if (!completeMenuItem.hasSubMenu()) {
                onItemClickListener?.invoke(menuItem)
            }
            true
        }
    }

    private fun addMainOptions() {
        menu.children.forEach { addOptionToBottomNavigationView(it) }
    }

    private fun addNestedOptionsForMenu(mainMenu: MenuItem) {
        viewBinding.flowTopItems.isVisible = mainMenu.hasSubMenu()
        if (!mainMenu.hasSubMenu()) return
        addNestedOptions(mainMenu.subMenu.children.toList())
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

    private fun addNestedOptions(options: List<MenuItem>) {
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

    private fun addNestedOption(option: MenuItem): Int {
        val textView = TextView(context).apply {
            layoutParams =
                LayoutParams(
                    context.resources.getDimensionPixelSize(R.dimen.bottom_nav_top_option_width),
                    LayoutParams.WRAP_CONTENT
                )
            text = option.title
            tag = option.itemId
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
        findViewById<View>(viewBinding.flowTopItems.referencedIds.first()).apply {
            isSelected = true
            performClick()
        }
    }

    override fun onClick(view: View) {
        clearSelection()
        view.isSelected = true
        val menuId = view.tag as Int
        onItemClickListener?.invoke(menuItems.first { it.itemId == menuId })
    }

    private fun clearSelection() {
        for (id in viewBinding.flowTopItems.referencedIds) {
            findViewById<TextView>(id).isSelected = false
        }
    }
}

fun StackedBottomNavigation.setupWithNavController(navController: NavController) {
    this.onItemClickListener = { menuItem ->
        NavigationUI.onNavDestinationSelected(menuItem, navController)
    }

    val navigationViewWeakReference = WeakReference(this)
    val navigationChangeListener = object : NavController.OnDestinationChangedListener  {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            val bottomNavView = navigationViewWeakReference.get()
            if (bottomNavView == null) {
                navController.removeOnDestinationChangedListener(this)
                return
            }

            menuItems.forEach { menuItem ->
                menuItem.isChecked = matchDestination(destination, menuItem.itemId)
            }
        }
    }
    navController.addOnDestinationChangedListener(navigationChangeListener)
}

fun matchDestination(destination: NavDestination, @IdRes destId: Int): Boolean {
    var currentDestination: NavDestination? = destination
    while (currentDestination!!.id != destId && currentDestination.parent != null) {
        currentDestination = currentDestination.parent
    }
    return currentDestination.id == destId
}