package pe.andy.bookholic.ui

import android.view.View
import android.view.animation.AlphaAnimation
import androidx.core.widget.NestedScrollView
import pe.andy.bookholic.databinding.ActivityMainBinding

class ScrollVerticalButton(private var mBinding: ActivityMainBinding) {

    private val showAnimation = AlphaAnimation(0f, 1f)
    private val hideAnimation = AlphaAnimation(1f, 0f)

    init {
        showAnimation.duration = 500
        hideAnimation.duration = 500

        with(mBinding) {
            goToTop.visibility = View.GONE
            goToTop.setOnClickListener {
                mBinding.nestedScrollView.smoothScrollTo(0, 0)
            }

            goToBottom.visibility = View.GONE
            goToBottom.setOnClickListener {
                nestedScrollView.smoothScrollTo(0, scrollViewContent.height)
            }
        }

        mBinding.nestedScrollView.setOnScrollChangeListener {
            v: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->

            val UP = -1
            val DOWN = 1

            val canScrollUp = v?.canScrollVertically(UP) ?: false
            val canScrollDown = v?.canScrollVertically(DOWN) ?: false

            // Top or Bottom
            if (canScrollUp xor canScrollDown) {
                hide()
            }
            // Middle
            else if (mBinding.goToTop.visibility == View.GONE) {
                show()
            }
        }
    }

    private fun show() {
        listOf(
            mBinding.goToTop,
            mBinding.goToBottom
        )
        .forEach {
            it.visibility = View.VISIBLE
            it.animation = showAnimation
            it.startAnimation(showAnimation)
        }
    }

    private fun hide() {
        listOf(
            mBinding.goToTop,
            mBinding.goToBottom
        ).forEach {
            it.visibility = View.GONE
            it.animation = hideAnimation
            it.startAnimation(hideAnimation)
        }
    }
}