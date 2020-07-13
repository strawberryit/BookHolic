package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension


object GangnamLibraryParser: LibraryParser, StringExtension {

    override fun parse(element: Element, library: Library): Ebook {
        return Ebook(library.name).apply {

            element.selectFirst("p.book_cover a")?.also {
                url = "${library.url}/books/${it.attr("href")}"
                thumbnailUrl = it.attrOfFirst("img", attr = "src")
            }

            platform = element.select("p.application span")
                    .elementAtOrNull(1)
                    ?.text()
                    ?: ""

            title = element.textOfFirst("h1.title a")
            author = element.textOfFirst("h2.writer")
            publisher = element.textOfFirst("h3.publisher")
            date = element.textOfFirst("h3.date")
                    .replace("출판일", "")

            // Count
            element.select("p.state span.number")
                    .also {
                        countTotal = it.textAt(0).toIntOnly(-1)
                        countRent = it.textAt(1).toIntOnly(-1)
                    }
        }
    }

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {

        val count = doc.select(".book_list p.location")
                .let {
                    it.select("font").remove()
                    it.text().toIntOnly(-1)
                }

        val page = doc.select(".book_list .paginate a")
                .last()
                ?.attr("href")
                ?.toIntOnly(-1)
                ?: -1

        return Pair(count, page)
    }
}