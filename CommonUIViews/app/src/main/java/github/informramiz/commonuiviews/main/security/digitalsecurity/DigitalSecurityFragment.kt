package github.informramiz.commonuiviews.main.security.digitalsecurity

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import github.informramiz.commonuiviews.R

class DigitalSecurityFragment : Fragment() {

    companion object {
        fun newInstance() = DigitalSecurityFragment()
    }

    private val viewModel: DigitalSecurityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.digital_security_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}