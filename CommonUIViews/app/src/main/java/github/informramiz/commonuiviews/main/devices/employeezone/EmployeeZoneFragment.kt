package github.informramiz.commonuiviews.main.devices.employeezone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import github.informramiz.commonuiviews.R

class EmployeeZoneFragment : Fragment() {

    companion object {
        fun newInstance() = EmployeeZoneFragment()
    }

    private val viewModel: EmployeeZoneViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.employee_zone_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}