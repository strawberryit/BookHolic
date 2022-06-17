package pe.andy.bookholic.test

import pe.andy.bookholic.model.Ebook

object TestData {

    fun generateTestBooks():  List<Ebook> {
        val b1 = Ebook("테스트 도서관")
        b1.apply {
            seq = "1234"
            title = "인프라 엔지니어의 교과서-시스템 구축과 관리편 테스트"
            author = "기술평론사 편집부"
            publisher = "길벗"
            libraryName = "테스트 도서관"
            platform = "교보문고"
            thumbnailUrl = "http://image.kyobobook.co.kr/images/book/large/093/l9791189909093.jpg"
            url = "http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946"
            countTotal = 5
            countRent = 2
            date = "2021-01-01"
        }

        val b2 = Ebook("테스트 도서관")
        b2.apply {
            seq = "5678"
            title = "인프라 엔지니어의 교과서-시스템 구축과 관리편 테스트"
            author = "기술평론사 편집부"
            publisher = "길벗"
            libraryName = "테스트 도서관"
            platform = "교보문고"
            thumbnailUrl = "http://image.kyobobook.co.kr/images/book/large/879/l9791162243879.jpg"
            url = "http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946"
            countTotal = 5
            countRent = 5
            date = "2019-01-01"
        }

        val b3 = Ebook("테스트 도서관")
        b3.apply {
            seq = "9101"
            title = "인프라 엔지니어의 교과서-시스템 구축과 관리편 테스트"
            author = "기술평론사 편집부"
            publisher = "길벗"
            libraryName = "테스트 도서관"
            platform = "교보문고"
            thumbnailUrl = "http://seoullib.barob.co.kr/resources/images/Lsize/PRD000115946.jpg"
            url = "http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946"
            countTotal = -1
            countRent = -1
            date = "2020-01-01"
        }

        return listOf(b1, b2, b3, b1, b2, b3)
    }
}