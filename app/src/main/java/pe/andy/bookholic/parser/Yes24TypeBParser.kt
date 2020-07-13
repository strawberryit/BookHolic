package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension
import pe.andy.bookholic.util.TextSlicer

object Yes24TypeBParser: LibraryParser, StringExtension {

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {

        // meta info
        val count = doc.textOfFirst("#list_tab em")
                .toIntOnly(-1)

        val page = doc.textOfFirst("#list_tab")
                .replace(""".*/| 페이지.*""".toRegex(), "")
                .toIntOnly(-1)

        return Pair(count, page)
    }

    override fun parse(element: Element, library: Library): Ebook {
        return Ebook(library.name).apply {
            // URL
            val href = element.attrOfFirst(".thumb2 > a", "href")
            url = "${library.url}${href}"

            // Thumbnail URL
            val src = element.attrOfFirst(".thumb2 > a > img", "src")
            thumbnailUrl = "${library.url}${src}"

            // Title
            title = element.textOfFirst(".info > .book_tit > a")

            // Infos
            val info: Elements = element.select(".info > .book_txt li")
            if (info.size > 3) {
                author = info.htmlAt(0)
                        .replace("\n".toRegex(), "")
                        .replace("""</.*>""".toRegex(), "")
                publisher = info.textAt(1)
                date = info.textAt(2)
                platform = info.textAt(3)
            }

            // "보유 1, 대출 0, 예약 0, ..."
            val text = element.textOfFirst(".info > .rentinfo")
            TextSlicer(text, regexp = ",").apply {
                countTotal = pop().toIntOnly(-1)
                countRent = pop().toIntOnly(-1)
            }
        }
    }
}