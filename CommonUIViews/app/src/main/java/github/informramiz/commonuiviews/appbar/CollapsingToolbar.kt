package github.informramiz.commonuiviews.appbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout
import github.informramiz.commonuiviews.databinding.CollapsingToolbarBinding


/**
 * Created by Ramiz Raja on 14/08/2020.
 */
class CollapsingToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr), AppBarLayout.OnOffsetChangedListener {

//    private val viewBinding: CollapsingToolbarBinding =
//        CollapsingToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        progress = -verticalOffset/appBarLayout?.totalScrollRange?.toFloat()!!
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (parent !is AppBarLayout) {
            throw IllegalArgumentException("The parent layout must be an AppBarLayout for ${CollapsingToolbar::class.simpleName} to work")
        }
        (parent as AppBarLayout).addOnOffsetUpdateListener { progress ->
            this.progress = progress
        }
    }
}

fun AppBarLayout.addOnOffsetUpdateListener(callback: (progress: Float) -> Unit) {
    addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        val seekPosition = -verticalOffset/totalScrollRange.toFloat()
        callback(seekPosition)
    })
}