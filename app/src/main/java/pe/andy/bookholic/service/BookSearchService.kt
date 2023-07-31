package pe.andy.bookholic.service

import android.view.View
import pe.andy.bookholic.databinding.FragmentSearchBinding
import pe.andy.bookholic.fragment.SearchFragment
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.searcher.SearchTask
import java.util.Objects
import java.util.stream.Collectors

class BookSearchService(
        private val searchFragment: SearchFragment,
        private val mBinding: FragmentSearchBinding
) {
    lateinit var query: SearchQuery
    var tasks: List<LibrarySearchTask>

    init {
        tasks = makeTasks()
    }

    fun makeTasks(): List<LibrarySearchTask> {
        val includeKakaoLibrary = mBinding.libraryListSwitchKakao.isChecked

        val libraries = SearchTask.libraries.let {
            if (includeKakaoLibrary) {
                it + SearchTask.kakaoLibrary
            } else {
                it
            }
        }

        return SearchTask.of(libraries, searchFragment)
            .sorted()
    }

    fun search(query: SearchQuery) {
        search(query, true)
    }

    fun search(query: SearchQuery, isFresh: Boolean) {
        this.query = query

        when(isFresh) {
            true -> searchInFirstTime()
            else -> searchProceeding()
        }

        tasks.shuffled()
            .stream()
            .parallel()
            .forEach{ it.execute() }
    }

    private fun searchInFirstTime() {
        mBinding.bookResultTitle.visibility = View.VISIBLE
        searchFragment.ebookViewModel.clearAllTable()

        tasks = makeTasks()
        query.page = 1
        setQueryOnAllTask(query)

        searchFragment.libraryAdapter.set(tasks)
    }

    private fun searchProceeding() {
        setQueryOnAllTask(query)

        tasks = tasks.stream()
                .parallel()
                .filter { it.resultCount > 0 }
                .filter { it.hasNext() }
                .map<LibrarySearchTask> { t ->
                    try {
                        t.cancel(true)

                        val nextTask = t.create().let {
                            it.query = query
                            it.query.page += 1
                            it
                        }

                        searchFragment.libraryAdapter.refresh()
                        nextTask
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                .filter(Objects::nonNull)
                .filter { it.isTaskPending }
                .collect(Collectors.toList())
    }

    private fun setQueryOnAllTask(query: SearchQuery) {
        tasks.forEach { it.query = query }
    }

    fun cancelAll() {
        tasks.forEach { it.cancel(true) }
    }

    fun isFinished(): Boolean {
        return tasks.asSequence()
                .none { it.isProgress }
    }

    fun isAllLastPage(): Boolean {
        return tasks.asSequence()
                .none { it.hasNext() }
    }

}
