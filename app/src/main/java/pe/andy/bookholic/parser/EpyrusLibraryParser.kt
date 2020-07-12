package pe.andy.bookholic.parser

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.model.Library
import pe.andy.bookholic.util.StringExtension

object EpyrusLibraryParser: LibraryParser, StringExtension {

    fun filter(element: Element): Boolean {
        return element.select("tr td")!!.size > 1
    }

    override fun parse(element: Element, library: Library): Ebook {

        val cells = element.select("tr td")!!

        return Ebook(library.name).apply {
            val href = cells.attrOfFirst("a", "href")
            url = "${library.url}/${href}"

            val src = cells.attrOfFirst("img", "src")
            thumbnailUrl = src

            cells.getOrNull(1)?.let {
                platform = it.textOfFirst("div", ".listbooktitle")
                title = it.textOfFirst("a")

                val infos: Elements = it.select("div.listtextr")
                author = infos.textAt(0)
                publisher = infos.textAt(2)
                date = infos.textAt(4)

                countRent = infos.textAt(5).toIntOnly(-1)
                countTotal = infos.textAt(7).toIntOnly(-1)
            }
        }
    }

    override fun parseMetaCount(doc: Document, library: Library): Pair<Int, Int> {
        val selector = "td.contents > table:nth-child(2) tr td:nth-child(2) div:nth-child(2) div"
        val count = doc.textOfFirst(selector)
                .toIntOnly(-1)

        val page = ((count - 1) / 8) + 1

        return Pair(count, page)
    }
}