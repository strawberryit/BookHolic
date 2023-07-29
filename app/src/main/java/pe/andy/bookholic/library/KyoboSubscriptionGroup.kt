package pe.andy.bookholic.library

import pe.andy.bookholic.fragment.SearchFragment
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.searcher.KyoboSubscriptionSearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

object KyoboSubscriptionGroup {
    private val libraries = listOf(
            Library(name = "영등포 구립도서관 (구독)", url = "https://ydplib.dkyobobook.co.kr"),
            Library(name = "용산구립도서관", url = "https://ebook.yslibrary.or.kr/elibrary-front"),
            Library(name = "서초구 전자도서관", url = "https://ebook.seocholib.or.kr", path="/elibrary-front"),
            Library(name = "중구 통합전자도서관", url = "https://ebook.junggulib.or.kr/elibrary-front"),
            Library(name = "광양시립도서관 (소장)", url = "https://ebook.gwangyang.go.kr:444", path="/elibrary-front"),
            Library(name = "광양시립도서관 (구독)", url = "https://gwangyanglib.dkyobobook.co.kr"),
            Library(name = "경기도사이버도서관 (구독)", url = "https://cyberlibrary.dkyobobook.co.kr"),
            Library(name = "김제시립도서관", url = "https://ebook-gjl.gimje.go.kr:444/elibrary-front/"),
            Library(name = "안산시립도서관 (구독)", url = "https://ansan.dkyobobook.co.kr"),
        )

    fun getLibraryList(searchFragment: SearchFragment): List<LibrarySearchTask> {
        return libraries.map {
            SoftReference(KyoboSubscriptionSearchTask(searchFragment = searchFragment, library = it))
                    .get()!!
        }
    }
}