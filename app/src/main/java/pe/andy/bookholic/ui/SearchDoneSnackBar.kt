package pe.andy.bookholic.ui

import com.google.android.material.snackbar.Snackbar
import pe.andy.bookholic.databinding.FragmentSearchBinding

class SearchDoneSnackBar(private val mBinding: FragmentSearchBinding) {

    fun show() {
        lateinit var snackbar: Snackbar
        snackbar = Snackbar
                .make(mBinding.layout, "검색이 완료되었습니다.", Snackbar.LENGTH_LONG)
                .setAction("확인") { snackbar.dismiss() }
        snackbar.show()
    }
}
