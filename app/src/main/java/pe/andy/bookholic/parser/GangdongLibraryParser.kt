package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension
import pe.andy.bookholic.util.TextSlicer


object GangdongLibraryParser: LibraryParser, StringExtension {

    override fun parse(element: Element, library: Library): Ebook {
        return Ebook(library.name).apply {

            // Thumbnail
            val src = element.attrOfFirst("div.cover img", attr = "src")
            thumbnailUrl = "${library.url}/${src}"

            element.selectFirst("div.info span.book_tit a")?.also {
                // Title
                title = it.text()

                // Url
                url = "${library.url}/${it.attr("href")}"
            }

            element.selectFirst("div.info ul.book_txt")?.also {
                // Author
                author = it.textOfFirst("li[title='저자']")

                // Publisher
                publisher = it.textOfFirst("li[title='출판사']")

                // Date
                date = it.textOfFirst("li[title='출간일']")

                // Platform
                platform = it.textOfFirst("li[title='공급사']")
                if (platform == "예스이십사") platform = "YES24"
            }

            val text = element.textOfFirst("div.info div.rentinfo")
            TextSlicer(text, ",").also {
                countTotal = it.pop().toIntOnly(-1)
                countRent = it.pop().toIntOnly(-1)
            }
        }
    }

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {
        val text: String = doc.select(".totalpage").text()

        TextSlicer(text, """[\(/]""").apply {
            val count = pop().toIntOnly(-1)
            pop()
            val page = pop().toIntOnly(-1)

            return Pair(count, page)
        }
    }
}