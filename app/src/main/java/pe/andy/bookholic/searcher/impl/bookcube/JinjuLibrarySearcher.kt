package pe.andy.bookholic.searcher.impl.bookcube

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.searcher.FxLibrarySearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

class JinjuLibrarySearcher(
        activity: MainActivity
) : FxLibrarySearchTask(
        activity, libraryName, baseUrl) {

    companion object {
        const val libraryName = "진주시립도서관"
        const val baseUrl = "http://125.135.250.131:8080"
    }

    override fun getLibraryCode() = "JinjuLibrary"

    override fun create(): LibrarySearchTask {
        return SoftReference(JinjuLibrarySearcher(mActivity))
                .get()!!
    }

}