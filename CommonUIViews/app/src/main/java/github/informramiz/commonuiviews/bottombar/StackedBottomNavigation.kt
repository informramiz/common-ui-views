package github.informramiz.commonuiviews.bottombar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import github.informramiz.commonuiviews.R
import github.informramiz.commonuiviews.databinding.ViewStackedBottomNavigationBinding


/**
 * Created by Ramiz Raja on 18/08/2020.
 */
class StackedBottomNavigation @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val viewBinding =
        ViewStackedBottomNavigationBinding.inflate(LayoutInflater.from(context), this, true)
}