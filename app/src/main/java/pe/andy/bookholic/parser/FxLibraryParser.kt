package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension
import pe.andy.bookholic.util.TextSlicer

object FxLibraryParser: LibraryParser, StringExtension {

    override fun parse(element: Element, library: Library): Ebook {
        return Ebook(library.name).apply {

            // Thumbnail
            thumbnailUrl = element.attrOfFirst(".thumb img", "src")

            // Title
            title = element.textOfFirst(".subject a")

            // URL
            val href = element.attrOfFirst(".subject a", "href")
            url = "${library.url}/${href}"

            // Book info
            element.select(".info .i1 li").also {
                author = it.textAt(0)
                        .replace(" 저$".toRegex(), "")

                publisher = it.textAt(1)
                date = it.textAt(2)

                platform = it.textAt(3)
                        .replace("""(공급 : |\(.+?\)|네트웍스.*| 전자책.*)""".toRegex(), "")
                        .trim()
            }

            // text: "대출 0/1"
            element.select(".info .i2 li").also {
                val text = it.textAt(0)
                TextSlicer(text, regexp = " |/").apply {
                    pop()
                    countRent = pop().toIntOnly(-1)
                    countTotal = pop().toIntOnly(-1)
                }
            }
        }
    }

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {

        val count = doc.select("#container > h2 > span > em")
                .text()
                .replace("""(총 |종.*)""".toRegex(), "")
                .toIntOnly(-1)

        val page = doc.select(".paging a.last")
                .attr("href")
                .replace(""".*,'|'\).*""".toRegex(), "")
                .toIntOnly(-1)

        return Pair(count, page)
    }
}