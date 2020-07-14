package pe.andy.bookholic.searcher

import android.os.AsyncTask
import okhttp3.Response
import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.SearchQuery
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset


abstract class LibrarySearchTask(
        protected var mActivity: MainActivity,
        var libraryName: String,
        protected var baseUrl: String
) : AsyncTask<Void?, Void?, List<Ebook>?>() {

    lateinit var query: SearchQuery

    var encoding: Charset = Encoding_UTF8

    var resultCount = -1
    var resultPageCount = -1

    var isError = false

    override fun onPreExecute() {
        searchStatus = LibrarySearchStatus.PROGRESS
        mActivity.libraryRecyclerList.refresh()
    }

    override fun doInBackground(vararg params: Void?): List<Ebook>? {

        try {
            request(query).use {
                val responseText = it.readText(encoding)
                if (responseText.isNotEmpty()) {
                    return parse(responseText)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        isError = true
        cancel(true)
        return emptyList()
    }

    @Suppress("unused")
    fun Response.readText(encoding: Charset): String {
        return if (encoding == Encoding_UTF8) {
            return this.body()?.string() ?: ""
        } else {
            BufferedReader(InputStreamReader(this.body()?.byteStream(), encoding))
                    .lineSequence()
                    .joinToString("\n")
        }

    }

    override fun onPostExecute(books: List<Ebook>?) {

        // Update Library list
        searchStatus = LibrarySearchStatus.DONE
        mActivity.libraryRecyclerList.refresh()

        // Update Book list
        val bookRecyclerList = mActivity.bookRecyclerList
        bookRecyclerList.add(books!!)

        //Log.d("BookHolic", this.libraryName + ": hasNext - " + this.hasNext());
        val service = mActivity.searchService
        val isFinished = service.isFinished()
        if (hasNext()) {
            bookRecyclerList.hideLoadProgress()
            bookRecyclerList.showLoadMore()
        } else {
            val isAllLastPage = service.isAllLastPage()

            //Log.d("BookHolic", this.libraryName + ": isFinished - " + isFinished + ", isAllLastPage - " + isAllLastPage);
            if (isFinished && isAllLastPage) {
                bookRecyclerList.hideLoadProgress()
                bookRecyclerList.hideLoadMore()
            }
        }
        if (isFinished) {
            mActivity.searchDoneSnackBar.show()
        }
    }

    override fun onCancelled() {
        searchStatus = LibrarySearchStatus.FAIL
        mActivity.libraryRecyclerList.refresh()
    }

    operator fun hasNext(): Boolean {
        return resultCount > 0 && query.page < resultPageCount
    }

    protected abstract fun getField(query: SearchQuery): String

    @Throws(IOException::class)
    protected abstract fun request(query: SearchQuery): Response

    @Throws(IOException::class)
    protected abstract fun parse(html: String): List<Ebook>
    abstract fun getLibraryCode(): String
    abstract fun create(): LibrarySearchTask

    var searchStatus = LibrarySearchStatus.INITIAL

    enum class LibrarySearchStatus {
        INITIAL, PROGRESS, DONE, FAIL
    }

    val isSearchDone: Boolean
        get() = searchStatus == LibrarySearchStatus.DONE

    val isProgress: Boolean
        get() = searchStatus == LibrarySearchStatus.PROGRESS

    val isTaskPending: Boolean
        get() = this.status == Status.PENDING

    companion object {
        const val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36"
        val Encoding_UTF8 = Charset.forName("UTF-8")
        val Encoding_EUCKR = Charset.forName("EUC-KR")
    }

}