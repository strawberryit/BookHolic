package pe.andy.bookholic.model

import pe.andy.bookholic.util.EncodingUtil
import java.nio.charset.Charset

data class Library(
    val name: String,
    val url: String,
    val path: String = "",
    val code: String = "",
    val type: LibraryType,
    val encoding: Charset = EncodingUtil.Encoding_UTF8
)
