package pe.andy.bookholic.ui

import android.view.View
import android.view.animation.AlphaAnimation
import androidx.core.widget.NestedScrollView
import pe.andy.bookholic.databinding.MainActivityBinding

class ScrollToTopButton(private var mBinding: MainActivityBinding) {

    private val showAnimation = AlphaAnimation(0f, 1f)
    private val hideAnimation = AlphaAnimation(1f, 0f)

    init {
        showAnimation.duration = 500
        hideAnimation.duration = 500

        mBinding.goToTop.visibility = View.GONE
        mBinding.goToTop.setOnClickListener {
            mBinding.nestedScrollView.smoothScrollTo(0, 0)
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
        mBinding.goToTop.visibility = View.VISIBLE
        mBinding.goToTop.animation = showAnimation
    }

    private fun hide() {
        mBinding.goToTop.visibility = View.GONE
        mBinding.goToTop.animation = hideAnimation
    }

}