package pe.andy.bookholic.searcher.impl

import android.icu.text.SimpleDateFormat
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.SearchField
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.parser.SeoulEduLibraryParser
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.util.HttpExtension
import pe.andy.bookholic.util.SslTrust.Companion.trustAllSslClient
import pe.andy.bookholic.util.StringExtension
import java.lang.ref.SoftReference
import java.util.*

class SeoulEduLibrarySearchTask(
        activity: MainActivity
) : LibrarySearchTask(activity, library),
        StringExtension, HttpExtension {

    init {
        this.encoding = library.encoding
    }

    companion object {
        val library = Library(
                name = "서울시교육청",
                url = "https://e-lib.sen.go.kr",
                encoding = Encoding_EUCKR)
    }

    override fun getLibraryCode() = ""

    override fun create(): LibrarySearchTask {
        return SoftReference<LibrarySearchTask>(
                SeoulEduLibrarySearchTask(mActivity)
        ).get()!!
    }

    override fun getField(query: SearchQuery): String {
        return when {
            (query.field === SearchField.PUBLISHER) -> "LOCATION"
            else -> query.field.toString()
        }
    }

    override fun request(query: SearchQuery): Response {

        val url = "${library.url}/wsearch/search_result.php"
        val req: Request = Request.Builder()
                .url(url)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .accept(Encoding_EUCKR)
                .userAgent(userAgent)
                .post(getFormBody(query))
                .build()

        return trustAllSslClient(OkHttpClient())
                .newCall(req)
                .execute()
    }

    private fun getFormBody(query: SearchQuery): RequestBody {

        val endDate: String = SimpleDateFormat("yyyyMMdd", Locale.US)
                .format(Calendar.getInstance().time)
        val startCount = (query.page - 1) * 5

        return FormBody.Builder()
                .add("sort", "RANK")
                .add("collection", "ALL")
                .add("range", "A")
                .add("startDate", "19700101")
                .add("endDate", endDate)
                .add("searchField", "ALL")
                .add("reQuery", "1")
                .add("startCount", startCount.toString())
                .addEncoded("query", query.keyword.encodeToEucKR())
                .build()
    }

    override fun parse(html: String): List<Ebook> {
        val doc: Document = Jsoup.parse(html)

        SeoulEduLibraryParser.parseMetaCount(doc, library).let {
            resultCount = it.first
            resultPageCount = it.second
        }

        if (resultCount == 0)
            return emptyList()

        return doc.select(".elib li")
                .asSequence()
                .map { SeoulEduLibraryParser.parse(it, library = library) }
                .toList()
    }
}
