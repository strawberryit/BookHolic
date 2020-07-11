package pe.andy.bookholic.searcher.impl.kyobo

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

class KyoboLibrarySearchTaskImpl(
        val mainActivity: MainActivity,
        val library: Library
) : KyoboLibrarySearchTask(mainActivity, library.name, library.url) {

    init {
        this.encoding = library.encoding
    }

    override fun create(): LibrarySearchTask {
        return SoftReference(
                KyoboLibrarySearchTaskImpl(mainActivity = mainActivity, library = library)
        ).get()!!
    }

    override fun getLibraryCode() = library.code
}