package github.informramiz.commonuiviews.bottombar

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import androidx.annotation.MenuRes
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
    companion object {
        const val NO_MENU_OPTION_ID = -1
    }

    var onItemClickListener: ((itemId: MenuItem) -> Unit)? = null
    private var isSelectedItemIdUpdating = false
    var selectedItemId: Int = NO_MENU_OPTION_ID
        set(value) {
            if (value == field || !menuItemsController.isValidMenuItem(value) || isSelectedItemIdUpdating) return // ignore double updates

            //lock the updates of this ID as this chang may require changing the BottomNavigationView
            // and changing BottomNavigationView can trigger its listeners and in turn update
            // this ID which we don't want
            isSelectedItemIdUpdating = true
            field = value
            onSelectedItemUpdated()
            isSelectedItemIdUpdating = false
        }

    private val viewBinding =
        ViewStackedBottomNavigationBinding.inflate(LayoutInflater.from(context), this)
    private val menuItemsController = MenuItemsController(context, this)

    @MenuRes
    private var menuResId: Int = NO_MENU_OPTION_ID
    private var uiUpdatesLocked = false

    init {
        extractAttributes(context, attrs)
        loadMenuOptions()
        registerListeners()
        selectedItemId = menuItemsController.firstMenuItem()?.itemId ?: NO_MENU_OPTION_ID
    }

    private fun onSelectedItemUpdated() {
        if (selectedItemId == NO_MENU_OPTION_ID) return
        notifyListeners()
        if (!uiUpdatesLocked) {
            selectMenuItem(selectedItemId)
        }
    }

    private fun notifyListeners() {
        val selectedMenuItem = menuItemsController.findMenuItem(selectedItemId)
        if (selectedMenuItem != null) {
            onItemClickListener?.invoke(selectedMenuItem)
        }
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
        menuItemsController.inflate(menuResId)
        addMainOptions()
    }

    private fun addMainOptions() {
        menuItemsController.mainMenuItems.forEach { addOptionToBottomNavigationView(it) }
    }

    private fun registerListeners() {
        viewBinding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            performUiLockedUpdate {
                updateMenuOptions(menuItem)
            }
            true
        }
    }

    private fun updateMenuOptions(menuItem: MenuItem) {
        val completeMenuItem = menuItemsController.findMainMenuItem(menuItem.itemId)
        selectedItemId = if (!completeMenuItem.hasSubMenu()) {
            clearExistingNestedOptions()
            completeMenuItem.itemId
        } else {
            addNestedOptionsForMenu(completeMenuItem)
            markFirstItemAsSelected()
        }
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

        viewBinding.flowTopItems.referencedIds = viewIds
    }

    private fun clearExistingNestedOptions() {
        viewBinding.flowTopItems.referencedIds.forEach { id ->
            removeView(findViewById(id))
        }
        viewBinding.flowTopItems.referencedIds = intArrayOf()
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

    @MenuRes
    private fun markFirstItemAsSelected(): Int {
        if (viewBinding.flowTopItems.referencedIds.isEmpty()) return NO_MENU_OPTION_ID
        val view = findViewById<View>(viewBinding.flowTopItems.referencedIds.first())
        view.isSelected = true
        return view.tag as Int
    }

    override fun onClick(view: View) {
        val menuId = view.tag as Int
        selectedItemId = menuId
    }

    private fun clearSelection() {
        for (id in viewBinding.flowTopItems.referencedIds) {
            findViewById<TextView>(id).isSelected = false
        }
    }

    private fun selectMenuItem(itemId: Int) {
        val (mainMenu, subMenu) = menuItemsController.findPossiblyNestedMenuItem(itemId) ?: return
        clearSelection()
        if (viewBinding.bottomNavigationView.selectedItemId != mainMenu.itemId) {
            viewBinding.bottomNavigationView.selectedItemId = mainMenu.itemId
        }
        subMenu ?: return
        selectSubMenuItem(subMenu)
    }

    private fun selectSubMenuItem(menuItem: MenuItem) {
        menuItem.isChecked = true
        viewBinding.flowTopItems.referencedIds.forEach { id ->
            val view = findViewById<View>(id)
            view.isSelected = view.tag == menuItem.itemId
        }
    }

    private fun performUiLockedUpdate(uiUpdate: () -> Unit) {
        uiUpdatesLocked = true
        uiUpdate()
        uiUpdatesLocked = false
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.selectedItemId = selectedItemId
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        selectedItemId = state.selectedItemId
    }

    private class SavedState : BaseSavedState {
        var selectedItemId: Int = StackedBottomNavigation.NO_MENU_OPTION_ID

        constructor(source: Parcel) : super(source) {
            selectedItemId = source.readInt()
        }

        constructor(parcelable: Parcelable?) : super(parcelable)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeInt(selectedItemId)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }

    }
}

fun StackedBottomNavigation.setupWithNavController(navController: NavController) {
    this.onItemClickListener = { menuItem ->
        NavigationUI.onNavDestinationSelected(menuItem, navController)
    }
    navController.addOnDestinationChangedListener(NavDestinationChangedListener(this))
}

private class NavDestinationChangedListener(stackedBottomNavigation: StackedBottomNavigation) : NavController.OnDestinationChangedListener {
    val navigationViewWeakReference = WeakReference(stackedBottomNavigation)

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val bottomNavView = navigationViewWeakReference.get()
        if (bottomNavView == null) {
            controller.removeOnDestinationChangedListener(this)
            return
        }

        bottomNavView.selectedItemId = destination.id
    }
}