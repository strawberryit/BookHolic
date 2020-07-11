package pe.andy.bookholic.model

import java.nio.charset.Charset

class Library(
        val name: String,
        val url: String,
        val code: String = "",
        val encoding: Charset = Encoding_UTF8
) {
    companion object {
        val Encoding_EUCKR: Charset = Charset.forName("EUC-KR")
        val Encoding_UTF8: Charset = Charset.forName("UTF-8")
    }
}