package pe.andy.bookholic.model

import java.nio.charset.Charset

class Yes24Library(
        name: String,
        url: String,
        code: String = "",
        encoding: Charset = Encoding_UTF8,
        val yes24Type: Yes24Type = Yes24Type.TypeB

): Library(name, url = url, path = "", code = code, encoding = encoding) {

    companion object {
        enum class Yes24Type {
            TypeA, TypeB
        }
    }
}