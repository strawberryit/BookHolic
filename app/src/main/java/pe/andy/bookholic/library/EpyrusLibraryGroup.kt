package pe.andy.bookholic.library

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.Library.Companion.Encoding_EUCKR
import pe.andy.bookholic.searcher.EpyrusLibrarySearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

object EpyrusLibraryGroup {
    private val libraries = listOf(
            Library(name = "양천구 e도서관", url = "http://ebook2.yangcheon.go.kr", encoding = Encoding_EUCKR)
    )

    fun getLibraryList(mainActivity: MainActivity): List<LibrarySearchTask> {
        return libraries.map {
            SoftReference(EpyrusLibrarySearchTask(mainActivity = mainActivity, library = it))
                    .get()!!
        }
    }
}