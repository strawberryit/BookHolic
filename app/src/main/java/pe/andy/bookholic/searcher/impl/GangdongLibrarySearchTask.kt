package pe.andy.bookholic.searcher.impl

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pe.andy.bookholic.fragment.SearchFragment
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.parser.GangdongLibraryParser
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.StringExtension
import java.io.IOException
import java.lang.ref.SoftReference
import java.util.*

class GangdongLibrarySearchTask(
        searchFragment: SearchFragment
) : LibrarySearchTask(searchFragment, library),
        StringExtension, HttpExtension {

    init {
        this.encoding = library.encoding
    }

    companion object {
        val library = Library(name = "강동구 전자도서관", url = "http://ebook.gdlibrary.or.kr:8090", encoding = Encoding_EUCKR)
    }

    override fun getLibraryCode() = library.code

    override fun create(): LibrarySearchTask {
        return SoftReference<LibrarySearchTask>(
                GangdongLibrarySearchTask(searchFragment)
        ).get()!!
    }

    override fun getField(query: SearchQuery): String {
        return query.field
                .toString()
                .toLowerCase(Locale.ROOT)
    }

    @Throws(IOException::class)
    override fun request(query: SearchQuery): Response {
        val req: Request = Request.Builder()
                .url(getUrl(query))
                .accept(Encoding_EUCKR)
                .userAgent(userAgent)
                .build()

        return OkHttpClient()
                .newCall(req)
                .execute()
    }

    private fun getUrl(query: SearchQuery): String {
        val url = "${library.url}/search/"

        return url.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameters(
                mapOf(
                    "srch_order" to getField(query),
                    "page_num" to query.page.toString(),
                    "view" to "10"
                )
            )
                .addEncodedQueryParameter("src_key", query.keyword.encodeToEucKR())
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
        val pair = GangdongLibraryParser.parseMetaCount(doc, library)
        resultCount = pair.first
        resultPageCount = pair.second
    }

    private fun parseBook(doc: Document): List<Ebook> {
        if (resultCount <= 0) {
            return emptyList()
        }

        return doc.select("div#booklist ul.list li.line")
                .asSequence()
                .map { GangdongLibraryParser.parse(it, library) }
                .toList()
    }


}