package pe.andy.bookholic.searcher

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.SearchField.Yes24LibrarySearchField.Companion.getValue
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.model.SortBy.Yes24LibrarySortBy.Companion.getValue
import pe.andy.bookholic.model.Yes24Library
import pe.andy.bookholic.model.Yes24Library.Companion.Yes24Type.TypeA
import pe.andy.bookholic.model.Yes24Library.Companion.Yes24Type.TypeB
import pe.andy.bookholic.parser.Yes24TypeAParser
import pe.andy.bookholic.parser.Yes24TypeBParser
import pe.andy.bookholic.util.EncodingUtil.Companion.toEuckr
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.SslTrust
import pe.andy.bookholic.util.StringExtension
import java.io.IOException
import java.lang.ref.SoftReference

class Yes24LibrarySearchTask(
        val mainActivity: MainActivity,
        val yes24Library: Yes24Library
) : LibrarySearchTask(mainActivity, yes24Library),
        StringExtension, HttpExtension {

    init {
        this.encoding = yes24Library.encoding
    }

    override fun getLibraryCode() = yes24Library.code

    override fun create(): LibrarySearchTask {
        return SoftReference<LibrarySearchTask>(
                Yes24LibrarySearchTask(mActivity, yes24Library)
        ).get()!!
    }

    override fun getField(query: SearchQuery): String {
        return getValue(query.field)
    }

    fun getSortBy(query: SearchQuery): String {
        return getValue(query.sortBy)
    }

    @Throws(IOException::class)
    override fun request(query: SearchQuery): Response {
        val req: Request = Request.Builder()
                .url(getUrl(query))
                .accept(yes24Library.encoding)
                .userAgent(userAgent)
                .build()

        return SslTrust.trustAllSslClient(OkHttpClient())
                .newCall(req)
                .execute()
    }

    private fun getUrl(query: SearchQuery): String {
        return when(yes24Library.yes24Type) {
            TypeA -> getUrlTypeA(query)
            TypeB -> getUrlTypeB(query)
        }
    }

    private fun getUrlTypeA(query: SearchQuery): String {
        val url = "${yes24Library.url}/ebook/search_list.asp"
        return url.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameters(
                mapOf(
                    "page_num" to query.page.toString(),
                    "keyoption2" to getField(query),
                    "sort" to getSortBy(query)
                )
            )
                .addEncodedQueryParameter("keyword", toEuckr(query.keyword))
                .build()
                .toString()
    }

    private fun getUrlTypeB(query: SearchQuery): String {
        val url = "${yes24Library.url}/search/"

        val keyword = when(yes24Library.encoding) {
            Encoding_EUCKR -> query.keyword.encodeToEucKR()
            else -> query.keyword
        }

        return url.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameters(
                mapOf(
                    "page_num" to query.page.toString(),
                    "srch_order" to "total"
                )
            )
                .addEncodedQueryParameter("src_key", keyword)
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
        val pair = when(yes24Library.yes24Type) {
            TypeA -> Yes24TypeAParser.parseMetaCount(doc, yes24Library)
            TypeB -> Yes24TypeBParser.parseMetaCount(doc, yes24Library)
        }
        resultCount = pair.first
        resultPageCount = pair.second
    }

    private fun parseBook(doc: Document): List<Ebook> {

        if (resultCount == 0)
            return emptyList()

        return when(yes24Library.yes24Type) {
            TypeA -> {
                doc.select("div.sub_main_total ul")
                        .asSequence()
                        .map { Yes24TypeAParser.parse(it, library = yes24Library) }
                        .toList()
            }
            TypeB -> {
                doc.select("#booklist .line")
                        .asSequence()
                        .map { Yes24TypeBParser.parse(it, library = yes24Library) }
                        .toList()
            }
        }
    }
}