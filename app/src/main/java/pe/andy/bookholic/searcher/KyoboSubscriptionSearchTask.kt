package pe.andy.bookholic.searcher

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pe.andy.bookholic.fragment.SearchFragment
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.SearchField.ZeroIndexSearchField.Companion.getValue
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.model.SortBy.KyoboLibrarySortBy.Companion.getValue
import pe.andy.bookholic.parser.KyoboSubscriptionParser
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.StringExtension
import java.io.IOException
import java.lang.ref.SoftReference


class KyoboSubscriptionSearchTask(
        override var searchFragment: SearchFragment,
        library: Library
): LibrarySearchTask(searchFragment, library),
        StringExtension, HttpExtension {

    init {
        this.encoding = library.encoding
    }

    override fun create(): LibrarySearchTask {
        return SoftReference(
                KyoboSubscriptionSearchTask(searchFragment = searchFragment, library = library)
        ).get()!!
    }

    override fun getLibraryCode() = library.code

    override fun getField(query: SearchQuery): String {
        return getValue(query.field).toString()
    }

    fun getSortBy(query: SearchQuery): String {
        return getValue(query.sortBy)
    }

    @Throws(IOException::class)
    override fun request(query: SearchQuery): Response {

        val url = getUrl(query)
        val client = OkHttpClient()
        val req: Request = Request.Builder()
                .url(url)
                .accept(encoding)
                .userAgent(userAgent)
                .build()
        return client.newCall(req).execute()
    }

    private fun getUrl(query: SearchQuery): String {
        val baseUrl = listOf(
                library.url,
                library.path,
                "/search/searchList.ink"
            )
            .filter { it.isNotEmpty() }
            .joinToString("")

        return baseUrl.toHttpUrlOrNull()!!
            .newBuilder()
            .addEncodedQueryParameter("schClst", "ctts%2Cautr%2Cpbcm")
            .addQueryParameters(
                mapOf(
                    "schDvsn" to "000",
                    "clstCheck" to "ctts",
                    "clstCheck" to "autr",
                    "clstCheck" to "pbcm",
                    "allDvsnCheck" to "000",
                    "dvsnCheck" to "001",
                    "selViewCnt" to 20,
                    "pageIndex" to query.pageString,
                    "recordCount" to 20
                )
            )
                .run {
                    val keyword = when(encoding) {
                        Encoding_EUCKR -> query.keyword.encodeToEucKR()
                        else -> query.keyword.encodeToUTF8()
                    }
                    addEncodedQueryParameter("schTxt", keyword)
                }
                .build()
                .toString()
    }

    @Throws(IOException::class)
    override fun parse(html: String): List<Ebook> {

        val doc: Document = Jsoup.parse(html)
        parseMetaCount(doc)
        return parseBook(doc)
    }

    private fun parseMetaCount(doc: Document) {
        val pair = KyoboSubscriptionParser.parseMetaCount(doc = doc, library = library)
        resultCount = pair.first
        resultPageCount = pair.second
    }

    private fun parseBook(doc: Document): List<Ebook> {
        return doc.select(".book_resultList > li")
                .asSequence()
                .map { KyoboSubscriptionParser.parse(element = it, library = library) }
                .toList()
    }
}