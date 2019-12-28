package pe.andy.bookholic.util

import java.net.URLEncoder

class EncodingUtil {
    companion object {

        @JvmStatic
        fun toEuckr(text: String): String {

            return try {
                URLEncoder.encode(text, "EUC-KR")
                        .replace(Regex("%25"), "%")
            }
            catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }
}