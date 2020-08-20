package github.informramiz.commonuiviews.bottombar

import android.animation.TimeInterpolator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewPropertyAnimator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import com.google.android.material.animation.AnimationUtils


/**
 * Created by Ramiz Raja on 18/08/2020.
 */
class HideOnScrollBehavior<V : View> @JvmOverloads constructor(
    context: Context? = null,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<V>(context, attrs) {
    companion object {
        private const val ENTER_ANIMATION_DURATION = 255L
        private const val EXIT_ANIMATION_DURATION = 175L
    }

    private var heightToScroll = 0f
    private var currentScrollState = ScrollState.SCROLLED_UP
    private var currentAnimator: ViewPropertyAnimator? = null

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        heightToScroll = if (isHeightAboveRequirement(child)) child.measuredHeight / 2f else 0f
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    private fun isHeightAboveRequirement(child: V) =
        child.measuredHeight >= minimumHeightToScroll(child.context)

    private fun minimumHeightToScroll(context: Context): Float {
        return 80f.toPx(context)
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        //We are only interested in Vertical Scroll
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (dyConsumed > 0) {
            slideDown(child)
        } else if (dyConsumed < 0) {
            slideUp(child)
        }
    }

    private fun animateChildTo(child: V, targetY: Float, duration: Long, interpolator: TimeInterpolator) {
        currentAnimator = child.animate()
            .translationY(targetY)
            .setDuration(duration)
            .setInterpolator(interpolator)
            .withEndAction {
                currentAnimator = null
            }
    }

    private fun slideUp(child: V) {
        if (currentScrollState == ScrollState.SCROLLED_UP) return
        currentAnimator?.let {
            it.cancel()
            child.clearAnimation()
        }

        currentScrollState = ScrollState.SCROLLED_UP
        animateChildTo(child, 0f, ENTER_ANIMATION_DURATION, AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR)
    }

    private fun slideDown(child: V) {
        if (currentScrollState == ScrollState.SCROLLED_DOWN) return
        currentAnimator?.let {
            it.cancel()
            child.clearAnimation()
        }

        currentScrollState = ScrollState.SCROLLED_DOWN
        animateChildTo(child, heightToScroll, EXIT_ANIMATION_DURATION, AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR)
    }

    private fun Float.toPx(context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
    }

    private enum class ScrollState {
        SCROLLED_UP,
        SCROLLED_DOWN
    }
}