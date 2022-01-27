package pe.andy.bookholic.library

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.searcher.KyoboSubscriptionSearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

object KyoboSubscriptionGroup {
    private val libraries = listOf(
            Library(name = "영등포 구립도서관 (구독)", url = "http://ydplib.dkyobobook.co.kr"),
            Library(name = "용산구립도서관", url = "http://ebook.yslibrary.or.kr/elibrary-front"),
            Library(name = "서초구 전자도서관", url = "http://ebook.seocholib.or.kr", path="/elibrary-front"),
            Library(name = "중구 통합전자도서관", url = "https://ebook.junggulib.or.kr/elibrary-front"),
        )

    fun getLibraryList(mainActivity: MainActivity): List<LibrarySearchTask> {
        return libraries.map {
            SoftReference(KyoboSubscriptionSearchTask(mainActivity = mainActivity, library = it))
                    .get()!!
        }
    }
}