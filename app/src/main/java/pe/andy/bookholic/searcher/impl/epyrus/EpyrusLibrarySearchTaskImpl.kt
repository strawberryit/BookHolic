package pe.andy.bookholic.searcher.impl.epyrus

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.searcher.EpyrusLibrarySearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

class EpyrusLibrarySearchTaskImpl(
        val mainActivity: MainActivity,
        val library: Library
) : EpyrusLibrarySearchTask(mainActivity, library.name, library.url) {

    init {
        this.encoding = library.encoding
    }

    override fun create(): LibrarySearchTask {
        return SoftReference(
                EpyrusLibrarySearchTaskImpl(mainActivity = mainActivity, library = library)
        ).get()!!
    }

    override fun getLibraryCode() = library.code
}