package pe.andy.bookholic.library

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Library.Companion.Encoding_EUCKR
import pe.andy.bookholic.model.Yes24Library
import pe.andy.bookholic.model.Yes24Library.Companion.Yes24Type.TypeA
import pe.andy.bookholic.model.Yes24Library.Companion.Yes24Type.TypeB
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.searcher.Yes24LibrarySearchTask
import java.lang.ref.SoftReference

object Yes24LibraryGroup {
    private val libraries = listOf(
            Yes24Library(name = "경주시립도서관", url = "http://library.gyeongju.go.kr:81", encoding = Encoding_EUCKR, yes24Type = TypeA),
            Yes24Library(name = "전라남도 도립도서관", url = "http://152.99.134.221:8080", encoding = Encoding_EUCKR, yes24Type = TypeB),
            Yes24Library(name = "울주통합도서관", url = "http://uljuebook.ulju.ulsan.kr", encoding = Encoding_EUCKR, yes24Type = TypeB),
            Yes24Library(name = "영천시립도서관", url = "http://www.yclib.go.kr:8080", encoding = Encoding_EUCKR, yes24Type = TypeA),
            Yes24Library(name = "여의도 전자책도서관", url = "http://ebook.yulib.or.kr", encoding = Encoding_EUCKR, yes24Type = TypeA),
            Yes24Library(name = "경산시립도서관", url = "http://elib.gbgs.go.kr", encoding = Encoding_EUCKR, yes24Type = TypeA),
            Yes24Library(name = "강서구 전자도서관", url = "https://ebook.gangseo.seoul.kr", encoding = Encoding_EUCKR, yes24Type = TypeB)
    )

    fun getLibraryList(mainActivity: MainActivity): List<LibrarySearchTask> {
        return libraries.map {
            SoftReference(Yes24LibrarySearchTask(mainActivity = mainActivity, yes24Library = it))
                    .get()!!
        }
    }
}