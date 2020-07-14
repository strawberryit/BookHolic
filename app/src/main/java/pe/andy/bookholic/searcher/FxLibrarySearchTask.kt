package pe.andy.bookholic.searcher

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.SearchField.ZeroIndexSearchField.Companion.getValue
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.parser.FxLibraryParser
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.StringExtension
import java.io.IOException
import java.lang.ref.SoftReference

class FxLibrarySearchTask(
        val mainActivity: MainActivity,
        library: Library
) : LibrarySearchTask(mainActivity, library),
        StringExtension, HttpExtension {

    init {
        this.encoding = library.encoding
    }

    override fun getLibraryCode() = library.code

    override fun create(): LibrarySearchTask {
        return SoftReference(
                FxLibrarySearchTask(mainActivity = mainActivity, library = library)
        ).get()!!
    }

    override fun getField(query: SearchQuery): String {
        return getValue(query.field).toString()
    }

    @Throws(IOException::class)
    override fun request(query: SearchQuery): Response {

        val req: Request = Request.Builder()
                .url(getUrl(query))
                .accept(Encoding_UTF8)
                .userAgent(userAgent)
                .build()

        return OkHttpClient()
                .newCall(req)
                .execute()
    }

    private fun getUrl(query: SearchQuery): String {
        val url = "${library.url}/FxLibrary/product/list"

        return HttpUrl.parse(url)!!.newBuilder()
                .addQueryParameters(mapOf(
                        "keyoption2" to getField(query),
                        "keyword" to query.keyword,
                        "page" to query.page.toString(),
                        "itemCount" to "20",
                        "searchoption" to "1",
                        "searchType" to "search"
                ))
                .build()
                .toString()
    }

    override fun parse(html: String): List<Ebook> {
        val doc = Jsoup.parse(html)

        parseMetaCount(doc)
        return when {
            (resultCount > 0) -> parseBook(doc)
            else -> emptyList()
        }
    }

    private fun parseMetaCount(doc: Document) {
        val pair = FxLibraryParser.parseMetaCount(doc, library = library)
        resultCount = pair.first
        resultPageCount = pair.second
    }

    private fun parseBook(doc: Document): List<Ebook> {
        return doc.select("#detail_list .item")
                .asSequence()
                .map { FxLibraryParser.parse(element = it, library = library) }
                .toList()
    }
}