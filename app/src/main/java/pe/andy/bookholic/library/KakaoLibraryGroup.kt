package pe.andy.bookholic.library

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.searcher.KyoboSubscriptionSearchTask
import pe.andy.bookholic.searcher.LibrarySearchTask
import java.lang.ref.SoftReference

object KakaoLibraryGroup {
    val library = Library(name = "카카오 전자도서관 (구독)", url = "http://kakaocorp.dkyobobook.co.kr", code = "Kakao")

    fun getLibraryList(mainActivity: MainActivity): List<LibrarySearchTask> {
        return listOf(
                SoftReference(KyoboSubscriptionSearchTask(mainActivity = mainActivity, library = library))
                        .get()!!
        )
    }
}