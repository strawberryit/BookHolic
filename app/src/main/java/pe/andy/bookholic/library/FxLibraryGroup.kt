package pe.andy.bookholic.library

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.searcher.FxLibrarySearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

object FxLibraryGroup {
    private val libraries = listOf(
            Library(name = "진주시립도서관", url = "http://125.135.250.131:8080")
    )

    fun getLibraryList(mainActivity: MainActivity): List<LibrarySearchTask> {
        return libraries.map {
            SoftReference(FxLibrarySearchTask(mainActivity = mainActivity, library = it))
                    .get()!!
        }
    }
}