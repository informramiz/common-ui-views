package github.informramiz.commonuiviews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import github.informramiz.commonuiviews.bottombar.setupWithNavController
import github.informramiz.commonuiviews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
//        supportActionBar?.hide()
        registerListeners()
        val navController = findNavController(R.id.navHost)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.secureZoneFragment,
            R.id.employeeZoneFragment,
            R.id.guestZoneFragment,
            R.id.digitalSecurityFragment,
            R.id.motionFragment,
            R.id.topologyFragment
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        viewBinding.stackBottomNavigation.setupWithNavController(navController)
    }

    private fun registerListeners() {

    }
}