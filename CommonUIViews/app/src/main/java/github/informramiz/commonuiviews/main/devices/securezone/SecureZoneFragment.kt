package github.informramiz.commonuiviews.main.devices.securezone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import github.informramiz.circleimageview.SimpleAdapter
import github.informramiz.commonuiviews.R
import github.informramiz.commonuiviews.cardscontainer.InteractableRecyclerView
import github.informramiz.commonuiviews.databinding.SecureZoneFragmentBinding

class SecureZoneFragment : Fragment() {

    companion object {
        fun newInstance() =
            SecureZoneFragment()
    }

    private val viewModel: SecureZoneViewModel by viewModels()
    private lateinit var viewBinding: SecureZoneFragmentBinding

    private val items = mutableListOf<String>().apply {
        for (i in 1..20) {
            add("Item$i")
        }
    }
    private val simpleAdapter = SimpleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return SecureZoneFragmentBinding.inflate(inflater, container, false).also { viewBinding = it }.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewBinding.cardsContainer.adapter = simpleAdapter
        viewBinding.cardsContainer.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        simpleAdapter.submitList(items)
        registerListeners()
    }

    private fun registerListeners() {
        viewBinding.cardsContainer.onInteractionListener = object : InteractableRecyclerView.OnChangeListener {
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