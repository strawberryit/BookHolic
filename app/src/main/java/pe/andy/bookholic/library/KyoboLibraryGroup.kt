package pe.andy.bookholic.library

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.Library.Companion.Encoding_EUCKR
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

object KyoboLibraryGroup {
    private val libraries = listOf(
            Library(name = "안산시 중앙도서관", url = "http://ebook.ansan.go.kr"),
            Library(name = "아산시립도서관", url = "http://elib.asan.go.kr:8090"),
            Library(name = "강북문화정보도서관", url = "http://ebook.gblib.or.kr"),
            Library(name = "강진군 전자도서관", url = "http://www.gjlib.go.kr:8080"),
            Library(name = "김제시립도서관", url = "http://ebook-gjl.gimje.go.kr:8000"),
            Library(name = "광양시립도서관", url = "http://ebook.gwangyang.go.kr:81"),
            Library(name = "인천서구 구립도서관", url = "http://ebook.issl.go.kr:8080"),
            Library(name = "서대문 이진아 기념도서관", url = "http://ebook.sdm.or.kr"),
            Library(name = "성북정보도서관", url = "http://elibrary.sblib.seoul.kr"),
            Library(name = "인천동구 송림도서관", url = "http://songlimlib.icdonggu.go.kr:8091"),
            Library(name = "순천시립도서관", url = "http://libebook.sc.go.kr", encoding = Encoding_EUCKR),
            Library(name = "여수시립도서관", url = "http://yslibeb.yeosu.go.kr"),
            Library(name = "의정부 전자도서관", url = "https://ebook.uilib.go.kr"),
            Library(name = "노원구 구립도서관", url = "http://eb.nowonlib.kr"),
            Library(name = "중랑구립정보도서관", url = "http://ebookjungnanglib.seoul.kr:8080/"),
            Library(name = "동대문구립도서관", url = "http://e-book.l4d.or.kr"),
            Library(name = "마포구 전자도서관", url = "http://ebook.mapo.go.kr:8088"),
    )

    fun getLibraryList(mainActivity: MainActivity): List<LibrarySearchTask> {
        return libraries.map {
            SoftReference(KyoboLibrarySearchTask(mainActivity = mainActivity, library = it))
                    .get()!!
        }
    }
}