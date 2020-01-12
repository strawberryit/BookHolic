package pe.andy.bookholic.model

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

data class SearchQuery (
        val keyword: String,
        val field: SearchField,
        val sortBy: SortBy) {

    var page: Int = 1

    val pageString: String
        get() = page.toString()

    fun getEncodedKeyword(encoding: String): String {
        return try {
            URLEncoder.encode(keyword, encoding)
        }
        catch (e: UnsupportedEncodingException) {
            ""
        }
    }

    fun getEucKRKeyword(): String {
        return this.getEncodedKeyword("EUC-KR")
    }


    fun nextPage(): SearchQuery {
        page += 1
        return this
    }
}
