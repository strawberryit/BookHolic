package pe.andy.bookholic.searcher.impl

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.parser.UijeongbuLibraryParser
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.StringExtension
import java.io.IOException
import java.lang.ref.SoftReference
import java.util.*

class UijeongbuLibrarySearchTask(
        activity: MainActivity
) : LibrarySearchTask(activity, library),
        StringExtension, HttpExtension {

    init {
        this.encoding = library.encoding
    }

    companion object {
        val library = Library(
                name = "의정부 전자도서관",
                url = "http://ebook.uilib.go.kr:8082")
    }

    override fun getLibraryCode() = library.code

    override fun create(): LibrarySearchTask {
        return SoftReference<LibrarySearchTask>(
                UijeongbuLibrarySearchTask(mActivity)
        ).get()!!
    }

    override fun getField(query: SearchQuery): String {
        return query.field
                .toString()
                .toLowerCase(Locale.getDefault())
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
        val url = "${library.url}/books/search.php"
        return HttpUrl.parse(url)!!.newBuilder()
                .addQueryParameters(mapOf(
                        "page" to query.page.toString(),
                        "wrd" to query.keyword
                ))
                .build()
                .toString()
    }

    @Throws(IOException::class)
    override fun parse(html: String): List<Ebook> {
        val doc = Jsoup.parse(html)

        UijeongbuLibraryParser.parseMetaCount(doc, library = library).let {
            resultCount = it.first
            resultPageCount = it.second
        }

        return doc.select(".sub22ListMain")
                .asSequence()
                .map { UijeongbuLibraryParser.parse(it, library = library) }
                .toList()
    }
}
