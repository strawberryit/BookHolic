package pe.andy.bookholic.searcher

import pe.andy.bookholic.fragment.SearchFragment
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.model.LibraryType.Gangdong
import pe.andy.bookholic.model.LibraryType.Gangnam
import pe.andy.bookholic.model.LibraryType.Gyunggido
import pe.andy.bookholic.model.LibraryType.Kyobo
import pe.andy.bookholic.model.LibraryType.KyoboSubscription
import pe.andy.bookholic.model.LibraryType.Sejong
import pe.andy.bookholic.model.LibraryType.Seoul
import pe.andy.bookholic.model.LibraryType.SeoulEdu
import pe.andy.bookholic.model.LibraryType.Yes24_A
import pe.andy.bookholic.model.LibraryType.Yes24_B
import pe.andy.bookholic.searcher.impl.GangdongLibrarySearchTask
import pe.andy.bookholic.searcher.impl.GangnamLibrarySearchTask
import pe.andy.bookholic.searcher.impl.GyunggidoCyberLibrarySearchTask
import pe.andy.bookholic.searcher.impl.SejongLibrarySearchTask
import pe.andy.bookholic.searcher.impl.SeoulEduLibrarySearchTask
import pe.andy.bookholic.searcher.impl.SeoulLibrarySearchTask
import pe.andy.bookholic.util.EncodingUtil
import java.lang.ref.SoftReference

object SearchTask {

    val kakaoLibrary = Library(name = "카카오 전자도서관 (구독)", url = "http://kakaocorp.dkyobobook.co.kr", type = KyoboSubscription, code = "Kakao")

    val libraries = listOf(
        // Kyobo Library
        Library(name = "안산시 중앙도서관 (소장)", url = "http://ebook.ansan.go.kr", type = Kyobo),
        //Library(name = "아산시립도서관", url = "http://elib.asan.go.kr:8090"),
        Library(name = "강북문화정보도서관", url = "https://ebook.gblib.or.kr", type = Kyobo),
        Library(name = "강진군 전자도서관", url = "http://www.gjlib.go.kr:8080", type = Kyobo),
        Library(name = "김제시립도서관", url = "http://ebook-gjl.gimje.go.kr:8000", type = Kyobo),
        Library(name = "인천서구 구립도서관", url = "http://ebook.issl.go.kr:8080", type = Kyobo),
        Library(name = "서대문 이진아 기념도서관", url = "https://ebook.sdm.or.kr", type = Kyobo),
        Library(name = "성북정보도서관", url = "http://elibrary.sblib.seoul.kr", type = Kyobo),
        Library(name = "인천동구 송림도서관", url = "http://songlimlib.icdonggu.go.kr:8091", type = Kyobo),
        Library(name = "의정부 전자도서관", url = "https://ebook.uilib.go.kr", type = Kyobo),
        Library(name = "노원구 구립도서관", url = "https://eb.nowonlib.kr", type = Kyobo),
        Library(name = "중랑구립정보도서관", url = "http://ebookjungnanglib.seoul.kr:8080/", type = Kyobo),
        Library(name = "동대문구립도서관", url = "https://e-book.l4d.or.kr", type = Kyobo),
        Library(name = "마포구 전자도서관", url = "http://ebook.mapo.go.kr:8088", type = Kyobo),

        // Kyobo Subscription library
        Library(name = "영등포 구립도서관 (구독)", url = "https://ydplib.dkyobobook.co.kr", type = KyoboSubscription),
        Library(name = "용산구립도서관", url = "https://ebook.yslibrary.or.kr/elibrary-front", type = KyoboSubscription),
        Library(name = "서초구 전자도서관", url = "https://ebook.seocholib.or.kr", path="/elibrary-front", type = KyoboSubscription),
        Library(name = "중구 통합전자도서관", url = "https://ebook.junggulib.or.kr/elibrary-front", type = KyoboSubscription),
        Library(name = "광양시립도서관 (소장)", url = "https://ebook.gwangyang.go.kr:444", path="/elibrary-front", type = KyoboSubscription),
        Library(name = "광양시립도서관 (구독)", url = "https://gwangyanglib.dkyobobook.co.kr", type = KyoboSubscription),
        Library(name = "경기도사이버도서관 (구독)", url = "https://cyberlibrary.dkyobobook.co.kr", type = KyoboSubscription),
        Library(name = "김제시립도서관", url = "https://ebook-gjl.gimje.go.kr:444/elibrary-front/", type = KyoboSubscription),
        Library(name = "안산시립도서관 (구독)", url = "https://ansan.dkyobobook.co.kr", type = KyoboSubscription),

        // Yes24 Library
        Library(name = "전라남도 도립도서관", url = "http://152.99.134.221:8080", encoding = EncodingUtil.Encoding_EUCKR, type = Yes24_B),
        Library(name = "울주통합도서관", url = "http://uljuebook.ulju.ulsan.kr", encoding = EncodingUtil.Encoding_EUCKR, type = Yes24_B),
        Library(name = "영천시립도서관", url = "http://www.yclib.go.kr:8080", encoding = EncodingUtil.Encoding_EUCKR, type = Yes24_A),
        Library(name = "여의도 전자책도서관", url = "https://ebook.ydplib.or.kr", encoding = EncodingUtil.Encoding_EUCKR, type = Yes24_A),
        Library(name = "경산시립도서관", url = "https://elib.gbgs.go.kr", encoding = EncodingUtil.Encoding_EUCKR, type = Yes24_A),
        Library(name = "강서구 전자도서관", url = "https://ebook.gangseo.seoul.kr", encoding = EncodingUtil.Encoding_EUCKR, type = Yes24_B),
        Library(name = "광주남구 통합도서관", url = "http://ebook.namgu.gwangju.kr:8080", encoding = EncodingUtil.Encoding_EUCKR, type = Yes24_A),

        // 서울시 전자도서관
        Library(name = "서울시 전자도서관", url = "https://elib.seoul.go.kr", type = Seoul, code = "SeoulLibrary"),

        // 강동구 전자도서관
        Library(name = "강동구 전자도서관", url = "http://ebook.gdlibrary.or.kr:8090", type = Gangdong, encoding = EncodingUtil.Encoding_EUCKR),

        // 강남구 전자도서관
        Library(name = "강남구 전자도서관", url = "https://ebook.gangnam.go.kr", type = Gangnam, encoding = EncodingUtil.Encoding_EUCKR),

        // "서울시교육청"
        Library(name = "서울시교육청", url = "https://e-lib.sen.go.kr", type = SeoulEdu, encoding = EncodingUtil.Encoding_EUCKR),

        // 경기도사이버도서관
        Library(name = "경기도사이버도서관", url = "https://www.library.kr", type = Gyunggido, encoding = EncodingUtil.Encoding_UTF8),

        // 국립세종도서관
        Library(name = "국립세종도서관", url = "https://ebook.nls.go.kr/", type = Sejong, encoding = EncodingUtil.Encoding_EUCKR),
    )

    fun of(libraries: List<Library>, searchFragment: SearchFragment): List<LibrarySearchTask> {
        return libraries.asSequence()
            .mapNotNull { of(it, searchFragment).get() }
            .toList()
    }

    fun of(library: Library, searchFragment: SearchFragment): SoftReference<LibrarySearchTask> {
        val task = when(library.type) {
            Gangdong -> GangdongLibrarySearchTask(library, searchFragment)
            Gangnam -> GangnamLibrarySearchTask(library, searchFragment)
            Gyunggido -> GyunggidoCyberLibrarySearchTask(library, searchFragment)
            Sejong -> SejongLibrarySearchTask(library, searchFragment)
            Seoul -> SeoulLibrarySearchTask(library, searchFragment)
            SeoulEdu -> SeoulEduLibrarySearchTask(library, searchFragment)
            Kyobo -> KyoboLibrarySearchTask(searchFragment, library)
            KyoboSubscription -> KyoboSubscriptionSearchTask(searchFragment, library)
            Yes24_A, Yes24_B -> Yes24LibrarySearchTask(searchFragment, library)
        }

        return SoftReference(task)
    }
}