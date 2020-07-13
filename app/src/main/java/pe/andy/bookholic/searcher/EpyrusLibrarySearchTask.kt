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
import pe.andy.bookholic.model.SearchField.EpyrusLibrarySearchField.Companion.getValue
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.parser.EpyrusLibraryParser
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.StringExtension
import java.io.IOException
import java.lang.ref.SoftReference


class EpyrusLibrarySearchTask(
        val mainActivity: MainActivity,
        val library: Library
): LibrarySearchTask(mainActivity, library.name, library.url),
        StringExtension, HttpExtension {

    init {
        this.encoding = library.encoding
    }

    override fun create(): LibrarySearchTask {
        return SoftReference(
                EpyrusLibrarySearchTask(mainActivity = mainActivity, library = library)
        ).get()!!
    }

    override fun getLibraryCode() = library.code

    override fun getField(query: SearchQuery): String {
        return getValue(query.field)
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
        val url = "${library.url}/book/searchlist.asp"

        val keyword = query.keyword.encodeToEucKR()
        return HttpUrl.parse(url)!!.newBuilder()
                .addQueryParameters(mapOf(
                        "SearchOption" to getField(query),
                        "pagenum" to query.page.toString()
                ))
                .addEncodedQueryParameter("SearchWord", keyword)
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
        val pair = EpyrusLibraryParser.parseMetaCount(doc = doc, library = library)
        resultCount = pair.first
        resultPageCount = pair.second
    }

    private fun parseBook(doc: Document): List<Ebook> {
        return doc.select("td.contents > table")
                .asSequence()
                .drop(2)
                .filter { EpyrusLibraryParser.filter(it) }
                .map { EpyrusLibraryParser.parse(element = it, library = library) }
                .toList()
    }
}