package pe.andy.bookholic.library

import pe.andy.bookholic.fragment.SearchFragment
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.LibraryType
import pe.andy.bookholic.searcher.KyoboSubscriptionSearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

object KakaoLibraryGroup {
    val library = Library(
        name = "카카오 전자도서관 (구독)",
        url = "http://kakaocorp.dkyobobook.co.kr",
        type = LibraryType.KyoboSubscription,
        code = "Kakao"
    )

    fun getLibraryList(searchFragment: SearchFragment): List<LibrarySearchTask> {
        return listOf(
                SoftReference(KyoboSubscriptionSearchTask(searchFragment = searchFragment, library = library))
                        .get()!!
        )
    }
}