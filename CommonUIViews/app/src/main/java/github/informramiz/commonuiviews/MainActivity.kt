package github.informramiz.commonuiviews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import github.informramiz.circleimageview.SimpleAdapter
import github.informramiz.commonuiviews.cardscontainer.InteractableRecyclerView
import github.informramiz.commonuiviews.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val items = mutableListOf("Item1", "Item2", "Item3", "Item4", "Item5", "Item6")
    private val simpleAdapter = SimpleAdapter()
    private val viewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        cardsContainer.adapter = simpleAdapter
        cardsContainer.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        simpleAdapter.submitList(items)
        registerListeners()
    }

    private fun registerListeners() {
        cardsContainer.onInteractionListener = object : InteractableRecyclerView.OnChangeListener {
            override fun onMoved(fromPosition: Int, toPosition: Int): Boolean {
                simpleAdapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(
                position: Int,
                direction: InteractableRecyclerView.SwipeDirection
            ) {
                items.removeAt(position)
                simpleAdapter.notifyItemRemoved(position)
            }
        }
    }
}