package pe.andy.bookholic.ui;

import com.google.android.material.snackbar.Snackbar;

import pe.andy.bookholic.databinding.MainActivityBinding;

public class SearchDoneSnackBar {

    MainActivityBinding mBinding;

    public SearchDoneSnackBar(MainActivityBinding mBinding) {
        this.mBinding = mBinding;
    }

    public void show() {
        Snackbar snackbar = Snackbar.make(mBinding.mainLayout, "검색이 완료되었습니다.", Snackbar.LENGTH_LONG);
        snackbar.setAction("확인", v -> {
            snackbar.dismiss();
        });
        snackbar.show();
    }
}
