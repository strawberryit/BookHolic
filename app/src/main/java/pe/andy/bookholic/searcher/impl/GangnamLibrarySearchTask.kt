package pe.andy.bookholic.searcher.impl

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.SearchField
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.parser.GangnamLibraryParser
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.SslTrust.Companion.trustAllSslClient
import pe.andy.bookholic.util.StringExtension
import java.io.IOException
import java.lang.ref.SoftReference

class GangnamLibrarySearchTask(
        activity: MainActivity
) : LibrarySearchTask(activity, library.name, library.url),
        StringExtension, HttpExtension {

    init {
        this.encoding = library.encoding
    }

    companion object {
        val library = Library(name = "강남구 전자도서관", url = "http://ebook.gangnam.go.kr", encoding = Library.Encoding_EUCKR)
        val searchFields: Map<SearchField, String> = mapOf(
                SearchField.ALL to "도서명",
                SearchField.TITLE to "도서명",
                SearchField.AUTHOR to "저자명",
                SearchField.PUBLISHER to "출판사명"
        )
    }

    override fun getLibraryCode() = library.code

    override fun create(): LibrarySearchTask {
        return SoftReference<LibrarySearchTask>(
                GangnamLibrarySearchTask(mActivity)
        ).get()!!
    }

    override fun getField(query: SearchQuery): String {
        return searchFields[query.field] ?: "도서명"
    }

    @Throws(IOException::class)
    override fun request(query: SearchQuery): Response {
        val req: Request = Request.Builder()
                .url(getUrl(query))
                .accept(Encoding_EUCKR)
                .userAgent(userAgent)
                .build()

        return trustAllSslClient(OkHttpClient())
                .newCall(req)
                .execute()
    }

    private fun getUrl(query: SearchQuery): String {
        val url = "${library.url}/books/book_info.asp"

        return HttpUrl.parse(url)!!.newBuilder()
                .addQueryParameters(mapOf(
                        "page_num" to query.page.toString(),
                        "list_num" to "20",
                        "ldav" to "off"
                ))
                .addEncodedQueryParameters(mapOf(
                        "bsc1" to "도서검색".encodeToEucKR(),
                        "bsc2" to getField(query).encodeToEucKR(),
                        "sw" to query.keyword.encodeToEucKR()
                ))
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
        val pair = GangnamLibraryParser.parseMetaCount(doc, library)
        resultCount = pair.first
        resultPageCount = pair.second
    }

    private fun parseBook(doc: Document): List<Ebook> {
        if (resultCount <= 0) {
            return emptyList()
        }

        return doc.select(".book_list .book")
                .asSequence()
                .map { GangnamLibraryParser.parse(it, library = library) }
                .toList()
    }
}