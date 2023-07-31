package pe.andy.bookholic.searcher

import android.os.AsyncTask
import android.view.View
import okhttp3.Response
import pe.andy.bookholic.fragment.SearchFragment
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.SearchQuery
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset


abstract class LibrarySearchTask(
    protected open var searchFragment: SearchFragment,
    val library: Library
) : AsyncTask<Void?, Void?, List<Ebook>?>(), Comparable<LibrarySearchTask> {

    lateinit var query: SearchQuery

    var encoding: Charset = Encoding_UTF8

    var resultCount = -1
    var resultPageCount = -1

    var isError = false

    override fun onPreExecute() {
        searchStatus = LibrarySearchStatus.PROGRESS
        searchFragment.libraryAdapter.refresh()
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
            return this.body?.string() ?: ""
        } else {
            BufferedReader(InputStreamReader(this.body?.byteStream(), encoding))
                    .lineSequence()
                    .joinToString("\n")
        }

    }

    override fun onPostExecute(books: List<Ebook>?) {

        // Update Library list
        searchStatus = LibrarySearchStatus.DONE
        searchFragment.libraryAdapter.refresh()

        // Update Book list
        searchFragment.ebookViewModel.upsert(books ?: emptyList())

        //Log.d("BookHolic", this.libraryName + ": hasNext - " + this.hasNext());
        val service = searchFragment.searchService
        val isFinished = service.isFinished()
        if (hasNext()) {
            searchFragment.hideLoadProgress()
            searchFragment.showLoadMore()
        } else {
            val isAllLastPage = service.isAllLastPage()

            //Log.d("BookHolic", this.libraryName + ": isFinished - " + isFinished + ", isAllLastPage - " + isAllLastPage);
            if (isFinished && isAllLastPage) {
                searchFragment.hideLoadProgress()
                searchFragment.hideLoadMore()
            }
        }
        if (isFinished) {
            searchFragment.searchDoneSnackBar.show()

            searchFragment.binding.fab.visibility = View.VISIBLE
            searchFragment.binding.fabCancel.visibility = View.GONE
        }
    }

    override fun onCancelled() {
        searchStatus = LibrarySearchStatus.FAIL
        searchFragment.libraryAdapter.refresh()
    }

    operator fun hasNext(): Boolean {
        return resultCount > 0 && query.page < resultPageCount
    }

    override fun compareTo(other: LibrarySearchTask): Int {
        return when {
            this.library.code == "Kakao" -> -2
            other.library.code == "Kakao" -> 2
            this.library.code == "SeoulLibrary" -> -1
            other.library.code == "SeoulLibrary" -> 1
            else -> this.library.name.compareTo(other.library.name)
        }
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