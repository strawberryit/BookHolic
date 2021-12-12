package pe.andy.bookholic.searcher.impl

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.SearchField.ZeroIndexSearchField.Companion.getValue
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.JsonExtension
import pe.andy.bookholic.util.StringExtension
import java.io.IOException
import java.lang.ref.SoftReference
import java.util.concurrent.TimeUnit.SECONDS

class SeoulLibrarySearchTask(
        activity: MainActivity
) : LibrarySearchTask(activity, library),
        StringExtension, HttpExtension, JsonExtension {

    init {
        this.encoding = library.encoding
    }

    companion object {
        val library = Library(
                name = "서울시 전자도서관",
                url = "http://elib.seoul.go.kr",
                code = "SeoulLibrary")
    }

    override fun getLibraryCode() = library.code

    override fun create(): LibrarySearchTask {
        return SoftReference<LibrarySearchTask>(
                SeoulLibrarySearchTask(mActivity)
        ).get()!!
    }

    override fun getField(query: SearchQuery): String {
        return getValue(query.field).toString()
    }

    @Throws(IOException::class)
    override fun request(query: SearchQuery): Response {

        val req: Request = Request.Builder()
                .url(getUrl(query))
                .addHeader("accept", "application/json;charset=UTF-8")
                .userAgent(userAgent)
                .build()

        return OkHttpClient.Builder()
                .connectTimeout(120L, SECONDS)
                .readTimeout(120L, SECONDS)
                .build()
                .newCall(req)
                .execute()
    }

    private fun getUrl(query: SearchQuery): String {
        val url = "${library.url}/ebooks/ContentsSearch.do"
        return url.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameters(
                mapOf(
                    "libCode" to 111314.toString(),
                    "searchKeyword" to query.keyword,
                    "currentCount" to query.page.toString(),
                    "searchOption" to getField(query),
                    "pageCount" to 20.toString(),
                    "userId" to "nologin",
                    "sType" to "TT",
                    "sortOption" to 1.toString()
                )
            )
                .build()
                .toString()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    @Throws(IOException::class)
    override fun parse(json: String): List<Ebook> {
        json.replace("([\r\n])".toRegex(), "").let {
            parseMetaCount(json)
            return parseBooks(json)
        }
    }

    private fun parseMetaCount(json: String) {
        resultCount = json.parseInt("/Contents/TotalCount")
        resultPageCount = json.parseInt("/Contents/TotalPage")
    }

    private fun parseBooks(json: String): List<Ebook> {
        return json.parseToListMap("/Contents/ContentDataList")
                .asSequence()
                .map { bookParser(it, library = library) }
                .toList()
    }

    private fun bookParser(map: Map<String, String>, library: Library): Ebook {
        return Ebook(library.name).apply {
            seq = map["ContentKey"] ?: ""
            title = map["ContentTitle"] ?: ""
            author = map["ContentAuthor"] ?: ""
            publisher = map["ContentPublisher"] ?: ""
            thumbnailUrl = map["ContentCoverUrlS"] ?: ""
            date = map["ContentPubDate"]?.replace(" 00:00:00.0", "") ?: ""
            platform = map["OwnerCodeDesc"] ?: ""
            url = "${library.url}/ebooks/detail.do?no=${seq}"
        }
    }

}