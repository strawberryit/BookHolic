package pe.andy.bookholic.library

import pe.andy.bookholic.fragment.SearchFragment
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.searcher.FxLibrarySearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

object FxLibraryGroup {
    private val libraries = emptyList<Library>()

    fun getLibraryList(searchFragment: SearchFragment): List<LibrarySearchTask> {
        return libraries.map {
            SoftReference(FxLibrarySearchTask(searchFragment = searchFragment, library = it))
                    .get()!!
        }
    }
}