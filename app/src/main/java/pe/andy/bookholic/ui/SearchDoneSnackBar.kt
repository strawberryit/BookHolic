package pe.andy.bookholic.ui

import com.google.android.material.snackbar.Snackbar

import pe.andy.bookholic.databinding.MainActivityBinding

class SearchDoneSnackBar(private val mBinding: MainActivityBinding) {

    fun show() {
        lateinit var snackbar: Snackbar
        snackbar = Snackbar
                .make(mBinding.mainLayout, "검색이 완료되었습니다.", Snackbar.LENGTH_LONG)
                .setAction("확인") { snackbar.dismiss() }
        snackbar.show()
    }
}
