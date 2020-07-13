package pe.andy.bookholic.service

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.library.EpyrusLibraryGroup
import pe.andy.bookholic.library.FxLibraryGroup
import pe.andy.bookholic.library.KyoboLibraryGroup
import pe.andy.bookholic.library.Yes24LibraryGroup
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.searcher.impl.*
import java.util.*
import java.util.stream.Collectors

class BookSearchService(
        val mActivity: MainActivity
) {
    lateinit var query: SearchQuery
    var tasks: List<LibrarySearchTask>

    init {
        tasks = makeTasks()
    }

    private fun makeTasks(): List<LibrarySearchTask> {
        return listOf(
                KyoboLibraryGroup.getLibraryList(mActivity),
                Yes24LibraryGroup.getLibraryList(mActivity),
                EpyrusLibraryGroup.getLibraryList(mActivity),
                FxLibraryGroup.getLibraryList(mActivity),
                listOf<LibrarySearchTask>(
                        SeoulLibrarySearchTask(mActivity),
                        GangdongLibrarySearchTask(mActivity),
                        GangnamLibrarySearchTask(mActivity),
                        SeoulEduLibrarySearchTask(mActivity),
                        GyunggidoCyberLibrarySearchTask(mActivity),
                        UijeongbuLibrarySearchTask(mActivity)
                )
        ).flatMap { it.toMutableList() }
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

        tasks.stream()
                .parallel()
                .forEach{ it.execute() }
    }

    private fun searchInFirstTime() {
        mActivity.bookRecyclerList.clear()

        tasks = makeTasks()
        query.page = 1
        setQueryOnAllTask(query)

        mActivity.libraryRecyclerList.set(tasks)
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

                        mActivity.libraryRecyclerList.refresh()
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
