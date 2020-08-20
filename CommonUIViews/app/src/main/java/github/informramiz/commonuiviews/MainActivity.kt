package github.informramiz.commonuiviews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        supportActionBar?.hide()
        registerListeners()
    }

    private fun registerListeners() {
        viewBinding.stackBottomNavigation.onItemClickListener = { menuItem ->
            val backgroundColorRes = if (menuItem.itemId == R.id.action_topology) {
                android.R.color.transparent
            } else {
                R.color.bottom_nav_background_color
            }
            viewBinding.stackBottomNavigation.setBackgroundResource(backgroundColorRes)
        }
    }
}