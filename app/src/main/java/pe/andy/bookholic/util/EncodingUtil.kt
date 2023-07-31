package pe.andy.bookholic.util

import java.net.URLEncoder
import java.nio.charset.Charset

object EncodingUtil {

    val Encoding_EUCKR: Charset = Charset.forName("EUC-KR")

    val Encoding_UTF8: Charset = Charset.forName("UTF-8")

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