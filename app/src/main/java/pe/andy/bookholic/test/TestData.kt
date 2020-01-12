package pe.andy.bookholic.test

import pe.andy.bookholic.model.Ebook

object TestData {

    fun generateTestBooks():  List<Ebook> {
        val b1 = Ebook("테스트 도서관")
                .setSeq("1234")
                .setTitle("인프라 엔지니어의 교과서-시스템 구축과 관리편 테스트")
                .setAuthor("기술평론사 편집부")
                .setPublisher("길벗")
                .setLibraryName("테스트 도서관")
                .setPlatform("교보문고")
                .setThumbnailUrl("http://seoullib.barob.co.kr/resources/images/Lsize/PRD000115946.jpg")
                .setUrl("http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946")
                .setCountTotal(5)
                .setCountRent(2)
                .setDate("2018-01-01")!!

        val b2 = Ebook("테스트 도서관")
                .setSeq("1234")
                .setTitle("인프라 엔지니어의 교과서-시스템 구축과 관리편")
                .setAuthor("기술평론사 편집부")
                .setPublisher("길벗")
                .setLibraryName("테스트 도서관")
                .setPlatform("교보문고")
                .setThumbnailUrl("http://elib.seoul.go.kr/resources/images/YES24/Lsize/8638926.jpg")
                .setUrl("http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946")
                .setCountTotal(5)
                .setCountRent(5)
                .setDate("2018-01-01")!!

        val b3 = Ebook("테스트 도서관")
                .setSeq("1234")
                .setTitle("인프라 엔지니어의 교과서-시스템 구축과 관리편")
                .setAuthor("기술평론사 편집부")
                .setPublisher("길벗")
                .setLibraryName("테스트 도서관")
                .setPlatform("교보문고")
                .setThumbnailUrl("http://elib.seoul.go.kr/resources/images/YES24/Lsize/7042064.jpg")
                .setUrl("http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946")
                .setCountTotal(5)
                .setCountRent(5)
                .setDate("2018-01-01")!!

        return listOf(b1, b2, b3, b1, b2, b3)
    }
}