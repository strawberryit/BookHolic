package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension


object UijeongbuLibraryParser: LibraryParser, StringExtension {

    override fun parse(element: Element, library: Library): Ebook {
        return Ebook(library.name).apply {

            // Thumbnail
            val src = element.attrOfFirst(".bookList img", attr = "src")
            thumbnailUrl = "${library.url}/${src}"

            // Title, URL
            element.select(".daList01 a")
                    .first()
                    ?.let {
                        title = it.text()

                        val href = it.attr("href")
                        url = "${library.url}/${href}"
                    }

            // Author, Publisher, Date
            element.select(".daList01")
                    .first()
                    ?.textNodes()
                    ?.let {
                        author = it.getOrNull(0)?.text() ?: ""
                        publisher = it.getOrNull(1)?.text() ?: ""
                        date = it.getOrNull(2)?.text() ?: ""
                    }

            // Platform, Counts
            element.select(".list_tb li").let {
                platform = it.elementAtOrNull(0)
                        ?.select(".sec")
                        ?.text() ?: ""

                countTotal = it.elementAtOrNull(1)
                        ?.select(".sec")
                        ?.text()
                        ?.toIntOnly(-1)?: -1

                countRent = it.elementAtOrNull(2)
                        ?.select(".sec")
                        ?.text()
                        ?.toIntOnly(-1)?: -1
            }
        }
    }

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {

        val count = doc.select(".sub001 .strong")
                .text()
                .toIntOnly(-1)

        val page = ((count - 1) / 5) + 1

        return Pair(count, page)
    }
}