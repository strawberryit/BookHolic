package pe.andy.bookholic.util

import android.content.Context
import androidx.core.content.ContextCompat
import pe.andy.bookholic.R
import pe.andy.bookholic.model.Ebook

class BookColor(mContext: Context) {

    private var bgRed: Int = ContextCompat.getColor(mContext, R.color.bgRed)
    private var bgOrange: Int = ContextCompat.getColor(mContext, R.color.bgOrange)
    private var bgGreen: Int = ContextCompat.getColor(mContext, R.color.bgGreen)
    private var bgMaroon: Int = ContextCompat.getColor(mContext, R.color.bgMaroon)
    private var bgLightBlue: Int = ContextCompat.getColor(mContext, R.color.bgLightBlue)
    private var bgPurple: Int = ContextCompat.getColor(mContext, R.color.bgPurple)
    private var bgGray: Int = ContextCompat.getColor(mContext, R.color.bgGray)

    fun getPlatformBGColor(platform: String): Int {
        return with(platform) {
            when {
                contains("교보") -> bgGreen
                contains("예스24") || contains("YES24", false) -> bgLightBlue
                contains("메키아") || contains("MEKIA", false) -> bgPurple
                contains("ECO") || contains("YB") -> bgMaroon
                contains("OPMS") -> bgOrange
                else -> bgGray
            }
        }
    }

    fun getRentStatusColor(book: Ebook): Int {
        return if (book.countRent < book.countTotal)
            this.bgGreen
        else
            this.bgRed
    }
}
