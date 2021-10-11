package pe.andy.bookholic.util

import android.content.Context
import androidx.core.content.ContextCompat
import pe.andy.bookholic.R
import pe.andy.bookholic.model.Ebook

class BookColor(mContext: Context) {

    var bgRed: Int = ContextCompat.getColor(mContext, R.color.bgRed)
    var bgOrange: Int = ContextCompat.getColor(mContext, R.color.bgOrange)
    var bgGreen: Int = ContextCompat.getColor(mContext, R.color.bgGreen)
    var bgMaroon: Int = ContextCompat.getColor(mContext, R.color.bgMaroon)
    var bgLightBlue: Int = ContextCompat.getColor(mContext, R.color.bgLightBlue)
    var bgPurple: Int = ContextCompat.getColor(mContext, R.color.bgPurple)
    var bgGray: Int = ContextCompat.getColor(mContext, R.color.bgGray)

    fun getPlatformBGColor(platform: String): Int {
        return with(platform) {
            when {
                contains("교보") -> bgGreen
                contains("예스24") || contains("YES24", false) -> bgLightBlue
                contains("메키아") || contains("MEKIA", false) -> bgPurple
                contains("ECO") || contains("YB") -> bgMaroon
                contains("OPMS") -> bgOrange
                contains("BC")
                        || contains("북큐브")
                        || contains("Bookcube", false) -> bgOrange
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
