package pe.andy.bookholic.searcher.impl

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.SearchField
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.parser.GyunggidoCyberLibraryParser
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.StringExtension
import java.io.IOException
import java.lang.ref.SoftReference


class GyunggidoCyberLibrarySearchTask(
        activity: MainActivity
) : LibrarySearchTask(activity, library),
        StringExtension, HttpExtension {

    init {
        this.encoding = library.encoding
    }

    companion object {
        val library = Library(
                name = "경기도사이버도서관",
                url = "http://www.library.kr",
                encoding = Encoding_UTF8)
    }

    override fun getLibraryCode() = ""

    override fun create(): LibrarySearchTask {
        return SoftReference<LibrarySearchTask>(
                GyunggidoCyberLibrarySearchTask(mActivity)
        ).get()!!
    }

    override fun getField(query: SearchQuery): String {
        return when {
            (query.field === SearchField.ALL) -> "text_idx"
            else -> query.field.toString()
        }
    }

    @Throws(IOException::class)
    override fun request(query: SearchQuery): Response {
        val req = Request.Builder()
                .url(getUrl(query))
                .accept(Encoding_UTF8)
                .userAgent(userAgent)
                .build()

        return OkHttpClient()
                .newCall(req)
                .execute()
    }

    private fun getUrl(query: SearchQuery): String {
        val url = "${library.url}/cyber/ebook/ebookList.do"
        return url.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameters(
                mapOf(
                    "searchKeyword" to query.keyword,
                    "searchCondition" to getField(query),
                    "currentPageNo" to query.page.toString(),
                    "viewType" to "desc",
                    "recordPagePerCount" to "30"
                )
            )
                .build()
                .toString()
    }

    override fun parse(html: String): List<Ebook> {
        val doc = Jsoup.parse(html)

        val pair = GyunggidoCyberLibraryParser.parseMetaCount(doc, library)
        resultCount = pair.first
        resultPageCount = pair.second

        if (resultCount <= 0)
            return emptyList()

        return doc.select("ul.resultList.descType > li")
                .asSequence()
                .map {GyunggidoCyberLibraryParser.parse(it, library = library) }
                .toList()
    }
}
