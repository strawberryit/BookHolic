package pe.andy.bookholic.library

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.Library.Companion.Encoding_EUCKR
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.searcher.impl.kyobo.KyoboLibrarySearchTaskImpl
import java.lang.ref.SoftReference

object KyoboLibraryGroup {
    private val libraries = listOf(
            Library(name = "안산시 중앙도서관", url = "http://ebook.ansan.go.kr"),
            Library(name = "아산시립도서관", url = "http://ebook.ascl.or.kr:8090"),
            Library(name = "강북문화정보도서관", url = "http://ebook.gblib.or.kr", encoding = Encoding_EUCKR),
            Library(name = "강진군 전자도서관", url = "http://www.gjlib.go.kr:8080"),
            Library(name = "김제시립도서관", url = "http://ebook-gjl.gimje.go.kr:8000"),
            Library(name = "김포시도서관", url = "http://ebook.gimpo.go.kr:8091"),
            Library(name = "광양시립도서관", url = "http://ebook.gwangyang.go.kr:81"),
            Library(name = "인천서구 구립도서관", url = "http://ebook.issl.go.kr:8080"),
            Library(name = "서대문 이진아 기념도서관", url = "http://ebook.sdm.or.kr"),
            Library(name = "서초구 전자도서관", url = "http://ebook.seocholib.or.kr", code = "SeochoLibrary"),
            Library(name = "성북정보도서관", url = "http://elibrary.sblib.seoul.kr", encoding = Encoding_EUCKR),
            Library(name = "인천동구 송림도서관", url = "http://songlimlib.icdonggu.go.kr:8091"),
            Library(name = "순천시립도서관", url = "http://libebook.sc.go.kr", encoding = Encoding_EUCKR),
            Library(name = "여수시립도서관", url = "http://yslibeb.yeosu.go.kr")
    )

    fun getLibraryList(mainActivity: MainActivity): List<LibrarySearchTask> {
        return libraries.map {
            SoftReference(KyoboLibrarySearchTaskImpl(mainActivity = mainActivity, library = it))
                    .get()!!
        }
    }
}