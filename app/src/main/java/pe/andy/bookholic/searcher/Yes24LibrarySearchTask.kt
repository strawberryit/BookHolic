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
import pe.andy.bookholic.model.LibraryType
import pe.andy.bookholic.model.SearchField.Yes24LibrarySearchField.Companion.getValue
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.model.SortBy.Yes24LibrarySortBy.Companion.getValue
import pe.andy.bookholic.parser.Yes24TypeAParser
import pe.andy.bookholic.parser.Yes24TypeBParser
import pe.andy.bookholic.util.EncodingUtil.toEuckr
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.SslTrust
import pe.andy.bookholic.util.StringExtension
import java.io.IOException
import java.lang.ref.SoftReference

class Yes24LibrarySearchTask(
        override var searchFragment: SearchFragment,
        library: Library
) : LibrarySearchTask(searchFragment, library),
        StringExtension, HttpExtension {

    init {
        this.encoding = library.encoding
    }

    override fun getLibraryCode() = library.code

    override fun create(): LibrarySearchTask {
        return SoftReference<LibrarySearchTask>(
                Yes24LibrarySearchTask(searchFragment, library)
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
                .accept(library.encoding)
                .userAgent(userAgent)
                .build()

        return SslTrust.trustAllSslClient(OkHttpClient())
                .newCall(req)
                .execute()
    }

    private fun getUrl(query: SearchQuery): String {
        return when(library.type) {
            LibraryType.Yes24_A -> getUrlTypeA(query)
            LibraryType.Yes24_B -> getUrlTypeB(query)
            else -> ""
        }
    }

    private fun getUrlTypeA(query: SearchQuery): String {
        val url = "${library.url}/ebook/search_list.asp"
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
        val url = "${library.url}/search/"

        val keyword = when(library.encoding) {
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
        val pair = when(library.type) {
            LibraryType.Yes24_A -> Yes24TypeAParser.parseMetaCount(doc, library)
            LibraryType.Yes24_B -> Yes24TypeBParser.parseMetaCount(doc, library)
            else -> (0 to 0)
        }
        resultCount = pair.first
        resultPageCount = pair.second
    }

    private fun parseBook(doc: Document): List<Ebook> {

        if (resultCount == 0)
            return emptyList()

        return when(library.type) {
            LibraryType.Yes24_A -> {
                doc.select("div.sub_main_total ul")
                        .asSequence()
                        .map { Yes24TypeAParser.parse(it, library = library) }
                        .toList()
            }
            LibraryType.Yes24_B -> {
                doc.select("#booklist .line")
                        .asSequence()
                        .map { Yes24TypeBParser.parse(it, library = library) }
                        .toList()
            }
            else -> emptyList()
        }
    }
}