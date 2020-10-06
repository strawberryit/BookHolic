package pe.andy.bookholic.library

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.searcher.KyoboSubscriptionSearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

object KyoboSubscriptionGroup {
    private val libraries = listOf(
            Library(name = "영등포 구립도서관 (구독)", url = "http://ydplib.dkyobobook.co.kr")
    )

    fun getLibraryList(mainActivity: MainActivity): List<LibrarySearchTask> {
        return libraries.map {
            SoftReference(KyoboSubscriptionSearchTask(mainActivity = mainActivity, library = it))
                    .get()!!
        }
    }
}