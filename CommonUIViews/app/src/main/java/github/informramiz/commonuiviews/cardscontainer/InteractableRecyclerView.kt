package github.informramiz.commonuiviews.cardscontainer

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * Created by Ramiz Raja on 14/08/2020.
 */
class InteractableRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var onInteractionListener: OnChangeListener? = null
    private val itemTouchHelper: ItemTouchHelper

    init {
        layoutManager = LinearLayoutManager(context)
        itemTouchHelper = ItemTouchHelper(getItemTouchHelperCallback(true))
        itemTouchHelper.attachToRecyclerView(this)
    }

    private fun getItemTouchHelperCallback(isSwipeEnabled: Boolean): ItemTouchHelper.Callback {
        val swipeDirection = if (isSwipeEnabled) ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT else 0
        return object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, swipeDirection
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                return onInteractionListener?.onMoved(fromPosition, toPosition) ?: false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                onInteractionListener?.onSwiped(viewHolder.adapterPosition, direction.toSwipeDirection())
            }

            private fun Int.toSwipeDirection(): SwipeDirection {
                for (direction in SwipeDirection.values()) {
                    if (direction.value == this) {
                        return direction
                    }
                }
                return SwipeDirection.IDLE
            }
        }
    }

    interface OnChangeListener {
        fun onMoved(fromPosition: Int, toPosition: Int): Boolean
        fun onSwiped(position: Int, direction: SwipeDirection) {}
    }

    enum class SwipeDirection(val value: Int) {
        IDLE(-1),
        LEFT(ItemTouchHelper.LEFT),
        RIGHT(ItemTouchHelper.RIGHT)
    }
}