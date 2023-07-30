package pe.andy.bookholic.searcher.impl

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import pe.andy.bookholic.fragment.SearchFragment
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.LibraryType
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
        searchFragment: SearchFragment
) : LibrarySearchTask(searchFragment, library),
        StringExtension, HttpExtension, JsonExtension {

    init {
        this.encoding = library.encoding
    }

    companion object {
        val library = Library(
            name = "서울시 전자도서관",
            url = "https://elib.seoul.go.kr",
            type = LibraryType.Seoul,
            code = "SeoulLibrary",
        )
    }

    override fun getLibraryCode() = library.code

    override fun create(): LibrarySearchTask {
        return SoftReference<LibrarySearchTask>(
                SeoulLibrarySearchTask(searchFragment)
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
        val url = "${library.url}/api/contents/search"
        return url.toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameters(
                mapOf(
                    "searchKeyword" to query.keyword,
                    "sortOption" to "1",
                    "contentType" to "EB",
                    "innerSearchYN" to "N",
                    "currentCount" to query.page.toString(),
                    "_" to System.currentTimeMillis()
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
        resultCount = json.parseInt("/totalCount")
        resultPageCount = json.parseInt("/totalPage")
    }

    private fun parseBooks(json: String): List<Ebook> {
        return json.parseToListMap("/ContentDataList")
                .asSequence()
                .map { bookParser(it, library = library) }
                .toList()
    }

    private fun bookParser(map: Map<String, String>, library: Library): Ebook {
        return Ebook(library.name).apply {
            seq = map["contentsKey"] ?: ""
            title = map["title"] ?: ""
            author = map["author"] ?: ""
            publisher = map["publisher"] ?: ""
            thumbnailUrl = map["coverSSizeUrl"] ?: ""
            date = map["publishDate"]?.replace(" 00:00:00.0", "") ?: ""
            platform = map["ownerCode"] ?: ""

            // https://elib.seoul.go.kr/contents/detail.do?no=PRD000142672
            url = "${library.url}/contents/detail.do?no=${seq}"
        }
    }

}