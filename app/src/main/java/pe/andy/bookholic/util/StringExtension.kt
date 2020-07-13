package pe.andy.bookholic.util

import java.net.URLEncoder

interface StringExtension {

    fun String.toIntOnly(defaultValue: Int = 0): Int {
        return try {
            this.replace("""\D*""".toRegex(), "")
                    .toInt()
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun String.encodeToEucKR(): String {
        return try {
            URLEncoder.encode(this, "EUC-KR")
                    .replace(Regex("%25"), "%")
        }
        catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}