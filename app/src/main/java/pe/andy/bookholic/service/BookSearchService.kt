package pe.andy.bookholic.service

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.searcher.impl.*
import pe.andy.bookholic.searcher.impl.bookcube.JinjuLibrarySearcher
import pe.andy.bookholic.searcher.impl.epyrus.YangCheonLibrarySearchTask
import pe.andy.bookholic.searcher.impl.kyobo.*
import pe.andy.bookholic.searcher.impl.yes24.*
import java.util.*
import java.util.stream.Collectors

class BookSearchService(
        val mActivity: MainActivity
) {
    var query: SearchQuery? = null
    var tasks: List<LibrarySearchTask>

    init {
        tasks = makeTasks()
    }

    private fun makeTasks(): List<LibrarySearchTask> {
        return mutableListOf(
                SeoulLibrarySearchTask(mActivity),
                YeouiDigitalLibrarySearchTask(mActivity),
                GangdongLibrarySearchTask(mActivity),
                GangnamLibrarySearchTask(mActivity),
                SeochoLibrarySearchTask(mActivity),
                SeodaemonLibrarySearchTask(mActivity),
                YeosuLibrarySearcher(mActivity),
                GyeongjuLibrarySearchTask(mActivity),
                YangCheonLibrarySearchTask(mActivity),
                SeoulEduLibrarySearchTask(mActivity),
                GyunggidoCyberLibrarySearchTask(mActivity),
                GyeongsanLibrarySearchTask(mActivity),
                AnsanLibrarySearchTask(mActivity),
                GangJinLibrarySearchTask(mActivity),
                UijeongbuLibrarySearchTask(mActivity),
                JeollanamdoLibrarySearchTask(mActivity),
                GimpoLibrarySearchTask(mActivity),
                SeongBukLibrarySearchTask(mActivity),
                AsanCityLibrarySearchTask(mActivity),
                IncheonSeoguLibrarySearchTask(mActivity),
                SongLimLibrarySearchTask(mActivity),
                GangbukCultureLibrarySearchTask(mActivity),
                GimjeLibrarySearchTask(mActivity),
                SuncheonLibrarySearchTask(mActivity),
                UljuLibrarySearchTask(mActivity),
                YeongcheonLibrarySearchTask(mActivity),
                GwangYangLibrarySearchTask(mActivity),
                JinjuLibrarySearcher(mActivity)
        )
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
        query?.page = 1
        setQueryOnAllTask(query!!)

        mActivity.libraryRecyclerList.set(tasks)
    }

    private fun searchProceeding() {
        setQueryOnAllTask(query!!)

        tasks = tasks.stream()
                .parallel()
                .filter { it.resultCount > 0 }
                .filter { it.hasNext() }
                .map<LibrarySearchTask> { t ->
                    try {
                        t.cancel(true)

                        var nextTask = t.create()
                        nextTask.query = query?.nextPage()

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
