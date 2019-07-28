package pe.andy.bookholic.ui;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import androidx.core.widget.NestedScrollView;

import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.databinding.MainActivityBinding;

public class ScrollToTopButton {

    MainActivityBinding mBinding;
    ImageView gotoTop;

    public ScrollToTopButton(MainActivity activity) {
        this.mBinding = activity.getMBinding();

        this.gotoTop = this.mBinding.goToTop;
        this.gotoTop.setVisibility(View.GONE);

        this.gotoTop.setOnClickListener(view -> {
            this.mBinding.nestedScrollView.smoothScrollTo(0, 0);
        });

        this.mBinding.nestedScrollView.setOnScrollChangeListener(
            (NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {

                final int UP = -1;
                final int DOWN = 1;

                boolean canScrollUp = v.canScrollVertically(UP);
                boolean canScrollDown = v.canScrollVertically(DOWN);

                // ON TOP
                if ((!canScrollUp) && canScrollDown) {
                    hide();
                }
                // ON BOTTOM
                else if (canScrollUp && (!canScrollDown)) {
                    hide();
                }
                else if (this.mBinding.goToTop.getVisibility() == View.GONE) {
                    show();
                }
        });
    }

    void show() {
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);
        this.mBinding.goToTop.setVisibility(View.VISIBLE);
        this.mBinding.goToTop.setAnimation(animation);
    }

    void hide() {
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        this.mBinding.goToTop.setVisibility(View.GONE);
        this.mBinding.goToTop.setAnimation(animation);
    }

}
