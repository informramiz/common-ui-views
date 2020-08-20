package github.informramiz.commonuiviews.main.devices.guestzone

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import github.informramiz.commonuiviews.R

class GuestZoneFragment : Fragment() {

    companion object {
        fun newInstance() = GuestZoneFragment()
    }

    private val viewModel: GuestZoneViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.guest_zone_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}