package pe.andy.bookholic.library

import pe.andy.bookholic.fragment.SearchFragment
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.Library.Companion.Encoding_EUCKR
import pe.andy.bookholic.model.LibraryType
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.searcher.Yes24LibrarySearchTask
import java.lang.ref.SoftReference

object Yes24LibraryGroup {
    private val libraries = listOf(
        Library(name = "전라남도 도립도서관", url = "http://152.99.134.221:8080", encoding = Encoding_EUCKR, type = LibraryType.Yes24_B),
        Library(name = "울주통합도서관", url = "http://uljuebook.ulju.ulsan.kr", encoding = Encoding_EUCKR, type = LibraryType.Yes24_B),
        Library(name = "영천시립도서관", url = "http://www.yclib.go.kr:8080", encoding = Encoding_EUCKR, type = LibraryType.Yes24_A),
        Library(name = "여의도 전자책도서관", url = "https://ebook.ydplib.or.kr", encoding = Encoding_EUCKR, type = LibraryType.Yes24_A),
        Library(name = "경산시립도서관", url = "https://elib.gbgs.go.kr", encoding = Encoding_EUCKR, type = LibraryType.Yes24_A),
        Library(name = "강서구 전자도서관", url = "https://ebook.gangseo.seoul.kr", encoding = Encoding_EUCKR, type = LibraryType.Yes24_B),
        Library(name = "광주남구 통합도서관", url = "http://ebook.namgu.gwangju.kr:8080", encoding = Encoding_EUCKR, type = LibraryType.Yes24_A),
    )

    fun getLibraryList(searchFragment: SearchFragment): List<LibrarySearchTask> {
        return libraries.map {
            SoftReference(Yes24LibrarySearchTask(searchFragment = searchFragment, library = it))
                    .get()!!
        }
    }
}