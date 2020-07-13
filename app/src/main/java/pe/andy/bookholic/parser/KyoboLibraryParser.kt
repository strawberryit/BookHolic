package pe.andy.bookholic.parser

import org.jsoup.nodes.Element
import org.jsoup.nodes.Document
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension
import pe.andy.bookholic.util.TextSlicer

object KyoboLibraryParser: LibraryParser, StringExtension {

    override fun parse(element: Element, library: Library): Ebook {
        return Ebook(library.name).apply {
            // URL
            element.selectFirst("p.pic a")?.also {
                url = it.attr("href")

                // Thumbnail URL
                val link = it.attrOfFirst(cssQuery = "img", attr = "src")
                thumbnailUrl = when {
                    link.startsWith("http") -> link
                    else -> "${library.url}/${link}"
                }
            }

            // Title, Platform
            element.selectFirst("dl > dt")
                    ?.also {
                        it.selectFirst("span.ico img")?.let {
                            platform = it.attr("alt").ifBlank {
                                it.attr("src").replace(""".*_|\.png""".toRegex(), "")
                            }
                        }
                        title = it.textOfFirst("a")
                    }

            // textAuthorPublisherDate: "   Author / [ Publisher / 2019-01-01 ]   "
            element.textOfFirst("dl > dd > em")
                    .replace("""[\[\]]""".toRegex(), "")
                    .also {
                        TextSlicer(it, "/").apply {
                            author = pop()
                            publisher = pop()
                            date = pop()
                        }
                    }

            // textCounts: 0/10
            element.textOfFirst("div.service ul li.loan span.num")
                    .also {
                        TextSlicer(it, "/").apply {
                            countRent = pop().toIntOnly(-1)
                            countTotal = pop().toIntOnly(-1)
                        }
                    }

            // 서초구 전자도서관 예외처리
            if (library.code == "SeochoLibrary") {
                platform = "교보문고"
            }
        }
    }

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {
        when(library.code) {
            // 서초구 전자도서관 예외처리
            "SeochoLibrary" -> {
                val count = doc.textOfFirst("p.result")
                        .replace(""".*\(""".toRegex(), "")
                        .toIntOnly(-1)

                val page = doc.textOfFirst(".total_count")
                        .toIntOnly(-1)

                return Pair(count, page)
            }
            else -> {
                val count = doc.textOfFirst(".list_sorting span.list_result strong")
                        .toIntOnly(-1)

                val page = doc.textOfFirst(".list_sorting span.total_count")
                        .toIntOnly(-1)

                return Pair(count, page)
            }
        }
    }
}