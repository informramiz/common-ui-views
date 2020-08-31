package github.informramiz.commonuiviews.bottombar

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children

class MenuItemsController(val context: Context, val view: StackedBottomNavigation) {
    private val menu: Menu = PopupMenu(context, view).menu
    private val menuInflater = MenuInflater(context)
    private val flattenedMenuItems: List<MenuItem>
        get() {
            return menu.children.flatMap {
                sequenceOf(it) + if (it.hasSubMenu()) {
                    it.subMenu.children
                } else {
                    emptySequence()
                }
            }.toList()
        }
    val mainMenuItems = menu.children

    fun inflate(menuResourceId: Int) {
        menuInflater.inflate(menuResourceId, menu)
    }

    fun firstMenuItem() = menu.children.firstOrNull()

    fun findMainMenuItem(itemId: Int): MenuItem? = menu.findItem(itemId)

    fun findPossiblyNestedMenuItem(itemId: Int): Pair<MenuItem, MenuItem?>? {
        menu.children.forEach { mainItem ->
            if (mainItem.itemId == itemId) {
                return Pair(mainItem, null)
            }

            if (mainItem.hasSubMenu()) {
                val subMenu = mainItem.subMenu.children.find { it.itemId == itemId }
                if (subMenu != null) {
                    return Pair(mainItem, subMenu)
                }
            }
        }

        return null
    }

    fun findMenuItem(itemId: Int) = flattenedMenuItems.firstOrNull { it.itemId == itemId }

    fun isValidMenuItem(itemId: Int)  = flattenedMenuItems.any { it.itemId == itemId }
}